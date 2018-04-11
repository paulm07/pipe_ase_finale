package analysis;

import analysis.spin.SpinTranslationConfig;
import formula.parser.ConditionBranchInfo;
import formula.parser.Formula2Promela;
import formula.parser.SetIteratorInfo;
import formula.parser.VariableDefinition;
import org.apache.commons.lang.StringUtils;
import pipe.dataLayer.*;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static analysis.QuantifierEvalInfo.QuantifierType;

/**
 * Created by Maks on 5/11/2016.
 */
public class PNPromela implements PNTranslator {
  private static final Pattern sPattern = Pattern.compile("\"([^\"]*)\"");

  public static final String SINGLE = "ONE";
  public static final String MULTIPLE = "MANY";
  private static final String BOUND_SINGLE_TEMPLATE = "#define " + SINGLE + " 1%n";
  private static final String BOUND_MULTIPLE_TEMPLATE  = "#define " + MULTIPLE + " %d%n";
  private static final String BLOCK_END_TEMPLATE = "};%n%n";

  //  P0.field1
  private static final String FIELD_NAME_TEMPLATE = "field%d";

  //  chan place_P0 = [Bound_P0] of {Type_x};
  private static final String CHAN_DEFINITION_TEMPLATE = "chan %s = [%s] of {%s};%n";

  private static final String ENABLED_CHECKER_START_TEMPLATE = "inline is_enabled_%s() {%n";

  private SpinTranslationConfig mConfig;

  private Set<String> mSourcesToClear = new HashSet<>(3);

  public PNPromela() {
  }

  public PNPromela(final SpinTranslationConfig pConfig) {
    mConfig = pConfig;
  }

  public void translate(final DataLayer pPNModel, final String pPropertySpecLTL, final Writer pTranslationWriter) throws IOException {
    if (mConfig == null) {
      mConfig = constructDefaultConfig(pPNModel);
    }
    defineBounds(pPNModel, pTranslationWriter);
    definePolls(pPNModel, pTranslationWriter);
    defineMTypes(pPNModel, pTranslationWriter);
    definePlaceDataStructures(pPNModel, pTranslationWriter);
    defineMessageChannels(pPNModel.getPlaces(), pTranslationWriter);

    defineSetUtilityFunctions(pTranslationWriter);
    defineTransitionFunctions(pPNModel, pTranslationWriter);
    defineMainFunction(pPNModel, pTranslationWriter);
    defineInitFunction(pPNModel, pTranslationWriter);
    pTranslationWriter.write(String.format("%nltl f { %s }%n%n", pPropertySpecLTL));
  }

  private SpinTranslationConfig constructDefaultConfig(final DataLayer pPNModel) {
    return SpinTranslationConfig.builder()
        .stringAs("mtype")
        .numberAs("byte")
        .withAtomicSequence("d_step")
        .havingGroupOf(Arrays.asList(pPNModel.getTransitions()).stream().map(t -> t.getName()).collect(Collectors.toList()))
        .build();
  }

  public static final String MESSAGE_TRANSFER_TEMPLATE =
            "inline transfer(source, dest, var) {%n" +
            "  do%n" +
            "  :: nempty(source) -> source?var; dest!var;%n" +
            "  od%n" +
            "}%n%n";

    public static final String MESSAGE_COPIER_TEMPLATE =
          "inline copy(source, destination, var) {%n" +
          "  for (var in source) {%n" +
          "    destination!var;%n" +
          "  }%n" +
          "}%n%n";

  public static final String MESSAGE_CLEANER_TEMPLATE =
      "inline clean(source, var) {%n" +
          "  do%n" +
          "  :: nempty(source) -> source?var%n" +
          "  od%n" +
          "}%n%n";
    private void defineSetUtilityFunctions(final Writer pTranslationWriter) throws IOException {
        pTranslationWriter.write(String.format(MESSAGE_CLEANER_TEMPLATE));
        pTranslationWriter.write(String.format(MESSAGE_COPIER_TEMPLATE));
        pTranslationWriter.write(String.format(MESSAGE_TRANSFER_TEMPLATE));
    }

  private void definePolls(final DataLayer pPNModel, final Writer pWriter) throws IOException {
    for (Place place : pPNModel.getPlaces()) {
      AtomicInteger counter = new AtomicInteger(1);
      final List<String> fields = place.getDataType().getTypes().stream().map(s -> String.format("p%d", counter.getAndIncrement())).collect(Collectors.toList());
      pWriter.write(String.format("#define %s(%s) place_%s??[%s]%n", place.getName(), fields.stream().collect(Collectors.joining(",")),
          place.getName(), fields.stream().map(s -> String.format("eval(%s)", s)).collect(Collectors.joining(","))));
    }
    pWriter.write(String.format("%n"));
  }

  private void defineMTypes(final DataLayer pPNModel, final Writer pWriter) throws IOException {
    Set<String> stringTokens = new HashSet<>();
    for (Place place : pPNModel.getPlaces()) {
      stringTokens.addAll(mTypesInPlace(place));
    }

    for (Transition transition : pPNModel.getTransitions()) {
      stringTokens.addAll(mTypesInTransition(transition));
    }

    if (stringTokens.size() > 0) {
      pWriter.write(String.format("mtype = {%s};%n%n", stringTokens.stream().collect(Collectors.joining(", "))));
    }
  }

  private Set<String> mTypesInTransition(final Transition pTransition) {
    Matcher matcher = sPattern.matcher(pTransition.getFormula());
    Set<String> tokens = new HashSet<>();
    while (matcher.find()) {
      tokens.add(PromelaUtil.stringToMType(matcher.group(1)));
    }

    return tokens;
  }

  private Set<String> mTypesInPlace(final Place pPlace) {
    Set<String> tokens = new HashSet<>();
    for (Token token : pPlace.getToken().getListToken()) {
      for (BasicType bt : token.Tlist) {
        if (bt.kind == BasicType.STRING) {
          tokens.add(PromelaUtil.stringToMType(bt.getValueAsString()));
        }
      }
    }

    return tokens;
  }

  private void defineInitFunction(final DataLayer pPNModel, final Writer pWriter) throws IOException {
    pWriter.write(String.format("init {%n"));
    for (Place place : pPNModel.getPlaces()) {
      for (Token token : place.getToken().getListToken()) {
        String sendExpression = PromelaUtil.tokenToSendExpression(token);//tokenToFieldAssignments(token, place, pWriter);
        pWriter.write(String.format("  place_%s!%s;%n", place.getName(), sendExpression));
      }
      pWriter.write(String.format("%n"));
    }
    String runString = mConfig.getGroups().stream().map(l -> String.format("  run process_%s();%n",l.get(0))).collect(Collectors.joining(""));
    String contRunString = mConfig.isHybrid()? "  run process_continuous();" : "";
    pWriter.write(String.format("  isInitialized = true;%n%s%s%n}%n%n", runString, contRunString));
  }

  private static final String FIELD_ASSIGNMENT_TEMPLATE = "  %s.field%d = %s;%n";

  private void writeTokenToFieldAssignments(final Token pToken, final Place pPlace, final Writer pWriter) throws IOException {
    int counter = 1;
    for (BasicType basicType : pToken.Tlist) {
      pWriter.write(String.format(FIELD_ASSIGNMENT_TEMPLATE, pPlace.getName(), counter++, PromelaUtil.stringToMType(basicType.getValueAsString())));
    }
  }

  protected void defineMainFunction(final DataLayer pDatalayer, final Writer pWriter) throws IOException {
    mConfig.getGroups().forEach(c -> {
      try {
        pWriter.write(String.format("proctype process_%s() {%n", c.get(0)));
        pWriter.write(String.format("%n  do%n"));
        for (String transitionName : c) {
          pWriter.write(String.format("  :: %s_statusIsUnknown -> %s()%n", transitionName, transitionName));
        }
        if (mConfig.getGroups().size() == 1 && !mConfig.isHybrid()) {
          pWriter.write(String.format("  :: !isEnabledAny -> break;%n"));
        }
        else {
          pWriter.write(String.format("  :: isEnabledAny -> skip;%n"));
          pWriter.write(String.format("  :: !isEnabledAny -> break;%n"));
        }

        pWriter.write(String.format("  od%n}%n%n"));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });

    List<Transition> continuous = Arrays.asList(pDatalayer.getTransitions()).stream()
        .filter(t -> t.isContinuous()).collect(Collectors.toList());
    if (continuous.size() > 0) {
      String cont_firing = continuous.stream().map(t -> String.format("%s();%n", t.getName())).collect(Collectors.joining("    "));
      pWriter.write(String.format("proctype process_continuous() {%n", continuous.get(0)));
      pWriter.write(String.format("%n  do%n"));
      pWriter.write(String.format("  :: isEnabledAny -> %s%n", cont_firing));
      pWriter.write(String.format("  :: else -> break;%n"));
      pWriter.write(String.format("  od%n}%n%n"));
    }
  }

  public static final String INLINE_FUNCTION_START = "inline %s(%s) {%n";
  public static final String INLINE_FUNCTION_END = "}%n%n";
  private static final String TRANSITION_EXECUTION_FUNCTION_BODY =
        "  %s%n" +
        "  %s%n" +
        "  atomic {%n" +
        "    is_enabled_%s();%n" +
        "  }%n";
  private static final String TRANSITION_EXECUTION_FUNCTION_TEMPLATE = INLINE_FUNCTION_START + TRANSITION_EXECUTION_FUNCTION_BODY + INLINE_FUNCTION_END;

  protected void defineTransitionExecutionFunctions(final Transition pTransition, final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
    String name = pTransition.getName();
    String declarations = getVariableDeclarations(name, pTranslator);
    String returnDeclarations = getReturnDeclarations(name, pTranslator);
//      String channelTemplate = "chan set_%s_%s = [%d] of {%s};%n";
    StringBuilder sb = new StringBuilder();
    pTranslator.getVariableDefinitions().stream().filter(vd -> vd.isSetVariable() && vd.isUsed()).forEach(vd -> {
      String channelName = PromelaUtil.channelNameForVariable(pTransition.getName(), vd.getVariableName());
      sb.append(getChannelDefinition(channelName, vd.getAccessorType(), vd.isSetVariable()));
    });
    pWriter.write(sb.toString());
    pWriter.write(String.format(INLINE_FUNCTION_START, name, ""));
    pWriter.write(String.format("  %s {%n", mConfig.getAtomicSequence()));
    pWriter.write(String.format("  %s%n", returnDeclarations));
    pWriter.write(String.format("  %s%n", declarations));
    defineTransitionEnabledCheckers(pTransition, pTranslator, pWriter);
    pWriter.write(String.format("  }%n"+INLINE_FUNCTION_END));
//    pWriter.write(String.format(TRANSITION_EXECUTION_FUNCTION_TEMPLATE, name, "", returnDeclarations, declarations, name));
//    pWriter.write(String.format(TRANSITION_EXECUTION_FUNCTION_TEMPLATE, name, "", returnDeclarations, declarations, name, name, name));
  }

    protected String getReturnDeclarations(final String pName, final Formula2Promela pTranslator) {
    final StringBuilder returnDeclarations = new StringBuilder();
    for (String rv : pTranslator.getInvokedFunction()) {
      returnDeclarations.append(PromelaUtil.getFunctionGenerator(rv).getReturnVariableDeclarations().stream().collect(Collectors.joining(";\n"))).append(";\n");
    }

    return returnDeclarations.toString();
  }

  protected String getVariableDeclarations(final String pName, final Formula2Promela pTranslator) {
    final StringBuilder declarations = new StringBuilder();
    pTranslator.getVariableDefinitions().stream().filter(vd -> !vd.isSetVariable()).forEach(vd -> {
      declarations.append(String.format("%s %s;%n  ", vd.getAccessorType(), vd.getVariableName()));
    });

    return declarations.toString();
  }

  private void defineFireTransitionFunctions(Transition pTransition, Formula2Promela pTranslator, Writer pWriter) throws IOException {
    List<ConditionBranchInfo> branches = pTranslator.getConditionalBranches();
    for(int i = 0; i < branches.size(); i++) {
      pWriter.write(String.format("inline fire_%s_%d() {%n", pTransition.getName(), i));
//    writeSetCreationStatements(pTranslator, pWriter);
      pTranslator.getVariableDefinitions().stream().filter(vd -> vd.isSetVariable() && vd.isInputVariable()).forEach(vd -> {
        try {
          String workingSetName = PromelaUtil.channelNameForVariable(pTransition.getName(), vd.getVariableName());
          String varName = "var_"+vd.getAccessorType();
          pWriter.write(String.format("  clean(%s, %s);%n", workingSetName, varName));
          pWriter.write(String.format("  transfer(place_%s, %s, %s);%n", vd.getInputPlaceNames().get(0), workingSetName, varName));
        } catch (IOException pE) {
          pE.printStackTrace();
        }
      });
      writeSetAssignments(pTranslator, pWriter);
      writeNonSetAssignments(pTranslator, branches.get(i).getPostCondition(), pWriter);
      pTranslator.getSetDefinitionInfo().forEach(info -> {
        try {
          pWriter.write(String.format(" clean(%s, var_%s);%n", info.getTarget(), info.getSetVariableType()));
        } catch (IOException pE) {
          pE.printStackTrace();
        }
      });
      pTranslator.getSourcesToClear().forEach(s -> {
        try {
          VariableDefinition vd = pTranslator.getVariableDefinition(s, false);
          pWriter.write(String.format(" clean(%s, var_%s);%n", PromelaUtil.channelNameForVariable(pTransition.getName(), s), vd.getAccessorType()));
        } catch (IOException pE) {
          pE.printStackTrace();
        }
      });
      for (String place : mSourcesToClear) {
        String variable = String.format("to_clean_%s", place);
        pWriter.write(String.format("  type_%s %s; ", place, variable));
        pWriter.write(String.format("  clean(place_%s, %s);%n", place, variable));
      }
      pWriter.write(String.format("  updateDependentsStatus_%s();%n}%n", pTransition.getName()));
    }

  }

  private void writeSetCreationStatements(final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
    for (SetDefinitionInfo sdInfo : pTranslator.getSetDefinitionInfo()) {
      pWriter.write(String.format("  %s(place_%s);%n", sdInfo.getFunctionName(), sdInfo.getTarget()));
    }
  }

  private void writeNonSetAssignments(final Formula2Promela pTranslator, final String pPostCondition, final Writer pWriter) throws IOException {
    List<VariableDefinition> nonSetVariables = pTranslator.getVariableDefinitions().stream()
        .filter(vd -> !vd.isSetVariable())
        .collect(Collectors.toList());

    nonSetVariables.forEach(vd -> {
      if (vd.isInputVariable() || vd.isOutputVariable()) {
        vd.getInputPlaceNames().forEach(s -> {
          try {
            pWriter.write(String.format("  place_%s??%s;%n", s, vd.getEvalSequence()));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      }
    });

    if (StringUtils.isNotBlank(pPostCondition)) {
      pWriter.write(String.format("%n%s;%n%n", pPostCondition));
    }

    nonSetVariables.forEach(vd -> {
      try {
        for (String place : vd.getOutputPlaceNames()) {
          pWriter.write(String.format("  place_%s!%s;%n", place, vd.getReceiveSequence()));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private boolean isIncludedInSetAssignment(final String pVariableName, final Map<String, String> pSetAssignments) {
    return pSetAssignments.containsKey(pVariableName) || pSetAssignments.containsValue(pVariableName);
  }

  //  do
//      ::  place_P0?P0;
//  P1.field1 = P0.field1;
//  P1.field2 = P0.field2;
//  place_P1!P1;
//  od
  private void writeSetAssignments(final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
//    for (Map.Entry<String, String> entry : pTranslator.getSetAssignments().entrySet()) {
//      VariableDefinition assignee = pTranslator.getVariableDefinition(entry.getKey(), false);
//      VariableDefinition assignment = pTranslator.getVariableDefinition(entry.getValue(), false);
////      pWriter.write(String.format("  %s;%n  %s;%n", assignee.getSetVariableType(), assignment.getSetVariableType()));
//      pWriter.write(String.format("  do%n  ::"));
//      pWriter.write(PromelaUtil.getMultiReceiveStatements(assignment, 4));
////      pWriter.write(PromelaUtil.receiveSequencesToAssignment(assignee.getReceiveSequence(), assignment.getReceiveSequence(), 4));
//      for (String output : assignee.getOutputPlaceNames()) {
//        pWriter.write(String.format("    place_%s!%s;%n", output, assignment.getVariableName()));
//      }
//      pWriter.write(String.format("  od%n"));
//    }
    System.out.println("POST:: "+pTranslator.getPostCondition());
  }

  public static final String TOKEN_COMBINATION_DETERMINER = "%%%dsfor (%s in place_%s) {%n%%s%%%ds}%n";
  private void defineTransitionEnabledCheckers(final Transition pTransition, final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
    StringBuilder checkingLoopBuilder = new StringBuilder();
    StringBuilder checkingTokenPresenceBuilder = new StringBuilder();
    StringBuilder initializeVariablesBuilder = new StringBuilder();
    StringBuilder multiplePresenceBuilder = new StringBuilder();
    int loopIndent = 6;
    String preTemplate = "%s%s%s";
    for (VariableDefinition vd : pTranslator.getVariableDefinitions()) {
      if (vd.getInputPlaceNames().size() > 0 && !vd.isSetVariable()) {
        Iterator<String> places = vd.getInputPlaceNames().iterator();
        String placeName = places.next();
        if (vd.isUsed()) {
          String forLoop = String.format(TOKEN_COMBINATION_DETERMINER, loopIndent, vd.getVariableName(), placeName, loopIndent);
          preTemplate = String.format(preTemplate, "", forLoop, "");
//        preTemplate = String.format(preTemplate, "", forLoop+loopBreak, "");
          loopIndent += 2;

          while (places.hasNext()) {
            checkingLoopBuilder.append(String.format("%s(%s) && ", places.next(), vd.getReceiveSequence()));
          }
        }
        else {
          checkingTokenPresenceBuilder.append(String.format("place_%s?[%s] && ", placeName, vd.getVariableName()));
          initializeVariablesBuilder.append(String.format("place_%s?<%s>; ", placeName, vd.getVariableName()));
          while (places.hasNext()) {
            multiplePresenceBuilder.append(String.format("%s(%s) && ", places.next(), vd.getReceiveSequence()));
          }
        }
      }
    }
    if (checkingTokenPresenceBuilder.length() > 4) {
      checkingTokenPresenceBuilder.setLength(checkingTokenPresenceBuilder.length() - 4);
    }

    if (multiplePresenceBuilder.length() > 4) {
      multiplePresenceBuilder.setLength(multiplePresenceBuilder.length() - 4);
    }

    StringBuilder quantifierEvalStmt = new StringBuilder();
    for (QuantifierEvalInfo evalInfo : pTranslator.getQuantifiersEvalInfo()) {
      quantifierEvalStmt.append(String.format("  %s = false;%n", evalInfo.getDeciderVariable()))
          .append(String.format("  %s();%n%n", evalInfo.getFunctionName()));
    }


    List<ConditionBranchInfo> branches = pTranslator.getConditionalBranches();

    StringBuilder capacityCheckingBuilder = new StringBuilder();
    List<Place> placesMayNeedCapacityCheck = new ArrayList<>(pTransition.getPlaceOutList());
    placesMayNeedCapacityCheck.removeAll(pTransition.getPlaceInList());
    for (Place place : placesMayNeedCapacityCheck) {
      if (!place.getDataType().getPow()) {
        capacityCheckingBuilder.append(String.format("empty(place_%s) && ", place.getName()));
      }
    }
    if (capacityCheckingBuilder.length() > 4) {
      capacityCheckingBuilder.setLength(capacityCheckingBuilder.length() - 4);
    }


//    pWriter.write(String.format("  bool enabledNonLoop = true;%n"));
    if (checkingTokenPresenceBuilder.length() > 0) {
      pWriter.write(String.format("  if%n  :: %s -> %s%n", checkingTokenPresenceBuilder.toString(), initializeVariablesBuilder.toString()));
      if (multiplePresenceBuilder.length() > 0) {
        pWriter.write(String.format("    %s%n", initializeVariablesBuilder.toString()));
        pWriter.write(String.format("%5sif%n%5s:: %s -> skip;%n%5s:: else -> enabledNonLoop = false;%n%5sfi%n",
            " "," ",multiplePresenceBuilder.toString(), " ", " "));
      }
      pWriter.write(String.format("  :: else -> %s_statusIsUnknown = false;%n  fi%n", pTransition.getName()));
    }

    if (capacityCheckingBuilder.length() > 0) {
      pWriter.write(String.format("%n  if%n  :: %s -> skip;%n  :: else -> goto checked_%s;%n  fi%n%n", capacityCheckingBuilder.toString(), pTransition.getName()));
    }

      pWriter.write(quantifierEvalStmt.toString());

      String startTemplate = String.format("%%%dsif%%n", loopIndent);
      String conditionTemplate = String.format("%%%ds:: %%s(%%s) -> fire_%%s_%%d();%%n", loopIndent, pTransition.getName());
//      String conditionTemplate = String.format("%%%ds:: %%s(%%s) -> updateEnabled(ID_%s, true); fire_%%s_%%d(); goto endfire;%%n", indent, pTransition.getName());

    StringBuilder sb = new StringBuilder();
      sb.append(String.format(startTemplate, ""));
      for (int i = 0; i < branches.size(); i++) {
        sb.append(String.format(conditionTemplate, "", checkingLoopBuilder.toString(), branches.get(i).getPrecondition(), pTransition.getName(), i));
      }

      String endTemplate = String.format("%%%ds:: else -> skip;%n%%%dsfi%n", loopIndent, loopIndent);
      sb.append(String.format(endTemplate, "", ""));

    pWriter.write(String.format("if%n  :: %s_statusIsUnknown == false -> skip;%n  :: else -> %n", pTransition.getName()));
    pWriter.write(String.format(preTemplate, "", sb.toString(), ""));
    pWriter.write(String.format("  fi%n"));
//      pWriter.write(String.format("  ID_%s=false;%n", pTransition.getName()));
//      pWriter.write(String.format("  updateEnabled();%n"));
//      pWriter.write(String.format("  checked_%s:%n  %s_statusIsUnknown = false;%n", pTransition.getName(), pTransition.getName()));
//      pWriter.write(String.format("  endOf_%s:%n  skip;%n", pTransition.getName()));
  }

//  public static final String ENABLE_ALL_TEMPLATE = "inline enableAll() {%n" +
//      "  %s  isEnabledAny = %s;%n" +
//      "}%n%n";
//  public static final String UPDATE_ENABLED_TEMPLATE = "inline updateEnabled() {%n" +
//    "  isEnabledAny = %s;%n" +
//    "}%n%n";
//  public static final String ENABLE_ALL_TEMPLATE = "inline enableAll() {%n" +
//    "  enabledAny = allEnabled;%n" +
//    "  isEnabledAny = true;%n" +
//    "}%n%n";
//  public static final String UPDATE_ENABLED_TEMPLATE = "inline disable(setBit) {%n" +
//      "  enabledAny = enabledAny&~setBit;%n" +
//      "  isEnabledAny = enabledAny>0;%n" +
//      "}%n%n";
  private void defineTransitionFunctions(final DataLayer pPNModel, final Writer pWriter) throws IOException {
    Set<String> mFunctionsInvoked = new HashSet<>(3);

    List<Transition> transitions = Arrays.asList(pPNModel.getTransitions());

    for (Transition transition : transitions) {
      pWriter.write(String.format("bool %s_statusIsUnknown = true;%n", transition.getName()));
    }
//    pWriter.write(String.format("int allEnabled = %d;%n", allEnabled));
//    pWriter.write(String.format("int enabledAny = %d;%n", allEnabled));
//    pWriter.write(String.format(UPDATE_ENABLED_TEMPLATE));
//    pWriter.write(String.format(ENABLE_ALL_TEMPLATE));
    pWriter.write(String.format("bool isEnabledAny = true;%n"));
    pWriter.write(String.format("bool isInitialized = false;%n"));
//    sb.setLength(sb.length()-4);
//    pWriter.write(String.format(UPDATE_ENABLED_TEMPLATE, sb.toString()));
//    pWriter.write(String.format(ENABLE_ALL_TEMPLATE, sbAll.toString(), sb.toString()));

    String enablesAll = transitions.stream()
        .map(t -> String.format("%s_statusIsUnknown", t.getName()))
        .collect(Collectors.joining(" || "));
    pWriter.write(String.format("%ninline updateEnabledAny() {%n  isEnabledAny = %s;%n}%n%n", enablesAll));

    for (Transition transition : transitions) {
      System.out.println("Translating "+transition.getName());
      mSourcesToClear.clear();
      Formula2Promela translator = PromelaUtil.evaluateFormula(transition);
      mFunctionsInvoked.addAll(translator.getInvokedFunction());
      defineQuantifierEvaluationFunctions(transition, translator, pWriter);
      defineSetCreationFunctions(translator, pWriter);
      defineUpdateDependentTransitionFunctions(transition, pWriter);
      defineFireTransitionFunctions(transition, translator, pWriter);
      defineTransitionExecutionFunctions(transition, translator, pWriter);
    }

    for (String function : mFunctionsInvoked) {
      PromelaUtil.getFunctionGenerator(function).generateFunctionDefinition(pWriter);
    }
  }

  private void defineUpdateDependentTransitionFunctions(final Transition pTransition, final Writer pWriter) throws IOException {
    pWriter.write(String.format("inline updateDependentsStatus_%s() {%n", pTransition.getName()));
    List<Transition> dependents = pTransition.getDependentTrans();
    for (Transition transition : dependents) {
      pWriter.write(String.format("  %s_statusIsUnknown = true;%n", transition.getName()));
    }
    if (dependents.isEmpty()) {
      pWriter.write(String.format("  updateEnabledAny();%n}%n%n"));
    }
    else {
      pWriter.write(String.format("  isEnabledAny = true;%n}%n%n"));
    }
  }

  private static final String SET_DEF_EVAL_FUNC_TEMPLATE =
      "inline %s() {%n" +
      "  %s %s;" +
      "  for (%s in %s) {%n" +
      "    if%n" +
      "    :: %s ->%s" +
      "       msg_chan!%s;%n" +
      "    :: else -> skip;%n" +
      "    fi%n" +
      "  }%n" +
      "}%n%n";
  private void defineSetCreationFunctions(final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
    for (SetDefinitionInfo sdInfo : pTranslator.getSetDefinitionInfo()) {
      pWriter.write(getChannelDefinition(sdInfo.getTarget(), sdInfo.getSetVariableType(), true));
      pWriter.write(String.format("inline %s() {%n", sdInfo.getFunctionName()));
      pWriter.write(String.format("  %s %s;%n", sdInfo.getSetVariableType(), sdInfo.getSetVariable()));
      writeSetCreationLoops(sdInfo, pWriter, 1);
      pWriter.write(String.format("}%n%n"));
    }
  }

  private String getChannelDefinition(final String pChannelName, final String pChannelType, final boolean pIsSet) {
    return String.format(CHAN_DEFINITION_TEMPLATE, pChannelName, pIsSet ? MULTIPLE : SINGLE, pChannelType);
  }

  private void writeSetCreationLoops(final SetDefinitionInfo pSdInfo, final Writer pWriter, final int depth) throws IOException {
    String pad = String.format(String.format("%%%ds", depth*2), "");
    String pad2 = String.format(String.format("%n%%%ds", depth*2+4), "");
    int size = pSdInfo.getNestedSources().size();
    if (depth > size) {
      String template = "%sif%n" +
              "%s:: %s -> %s %s!%s;%n" +
              "%s:: else -> skip;%n" +
              "%sfi;%n";
      List<String> linesInPost = Arrays.asList(pSdInfo.getPostCondition().split(String.format("%n")));
      pWriter.write(String.format(template, pad, pad,
              pSdInfo.getPreCondition(),
              linesInPost.stream().collect(Collectors.joining(pad2)),
              pSdInfo.getTarget(),
              pSdInfo.getSetVariable(),
              pad, pad));
    }
    else {
      String template = "%s%s %s;%n%sfor (%s in %s) {%n";
      SetIteratorInfo info = pSdInfo.getNestedSources().get(size-depth);
      pWriter.write(String.format(template, pad, info.getVariableType(), info.getVariable(), pad, info.getVariable(), info.getSource()));
      writeSetCreationLoops(pSdInfo, pWriter, depth+1);
      pWriter.write(String.format("%s}%n", pad));
    }
  }

  private static final String QUANT_EVAL_FUNC_TEMPLATE =
      "inline %s() {%n" +
      "  %s = %s;%n" +
      "  %s %s;%n" +
      "  for (%s in %s) {%n" +
      "    %s%n" +
      "    if%n" +
      "    :: %s -> %s = %s; break;%n" + // !(precondition) for universal and precondition for existential
      "    :: else -> skip%n" +
      "    fi%n" +
      "  }%n" +
      "}%n%n";
  private void defineQuantifierEvaluationFunctions(final Transition pTransition, final Formula2Promela pTranslator, final Writer pWriter) throws IOException {
    for (QuantifierEvalInfo evalInfo : pTranslator.getQuantifiersEvalInfo()) {
      boolean isForAll = evalInfo.getType() == QuantifierType.FORALL;
      String loopVariable = evalInfo.getLoopVariableName();
      String nestedQuantifierCalls = evalInfo.getNestedQuantifiers().stream().collect(Collectors.joining(String.format("();%n    ")));
      if (StringUtils.isNotBlank(nestedQuantifierCalls)) {
        nestedQuantifierCalls = String.format("%s();%n", nestedQuantifierCalls);
      }
      pWriter.write(String.format("bool %s;%n", evalInfo.getDeciderVariable()));
      pWriter.write(String.format(QUANT_EVAL_FUNC_TEMPLATE,
          evalInfo.getFunctionName(),
          evalInfo.getDeciderVariable(),
          isForAll ? "true" : "false",
          evalInfo.getLoopVariableType(),
          loopVariable,
          loopVariable,
          PromelaUtil.channelNameForVariable(pTransition.getName(), evalInfo.getSourceName()),
          nestedQuantifierCalls,
          isForAll ? "!("+evalInfo.getCondition()+")" : evalInfo.getCondition(),
          evalInfo.getDeciderVariable(),
          isForAll ? "false" : "true"));
    }
  }

  private void defineMessageChannels(final Place[] pPlaces, final Writer pWriter) throws IOException {
    for (Place place : pPlaces) {
      String name = place.getName();
      DataType dataType = place.getDataType();
      pWriter.write(getChannelDefinition("place_"+name, PromelaUtil.dataTypeToPromelaType(dataType), dataType.getPow()));
    }
    pWriter.write(String.format("%n"));
  }

  private void definePlaceDataStructures(DataLayer pPNModel, Writer pWriter) throws IOException {
    Set<String> uniqueTypes = new HashSet<>();
    for (Place place : pPNModel.getPlaces()) {
      uniqueTypes.add(PromelaUtil.dataTypeToPromelaType(place.getDataType()));
    }
    uniqueTypes.stream().sorted().forEach(s -> {
      try {
        pWriter.write(PromelaUtil.getPromelaTypeStructureDefinition(s));
          pWriter.write(String.format("%s var_%s;%n%n", s, s));
      } catch (IOException pE) {
        pE.printStackTrace();
      }
    });

  }

  private void defineBounds(final DataLayer pPNModel, final Writer pWriter) throws IOException {
    int maxCapacity = 0;
    for (Place place : pPNModel.getPlaces()) {
      if (maxCapacity < place.getCapacity()) {
        maxCapacity = place.getCapacity();
      }
    }
    maxCapacity = 2*maxCapacity;
    pWriter.write(String.format(BOUND_SINGLE_TEMPLATE+"%n"));
    pWriter.write(String.format(BOUND_MULTIPLE_TEMPLATE+"%n%n", maxCapacity));
  }
}
