package analysis;

import formula.FormulaUtil;
import formula.absyntree.Sentence;
import formula.parser.ErrorMsg;
import formula.parser.Formula2Promela;
import formula.parser.VariableDefinition;
import org.apache.commons.lang.StringUtils;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataType;
import pipe.dataLayer.Token;
import pipe.dataLayer.Transition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 *
 * Created by Maks on 5/20/2016.
 */
public final class PromelaUtil {

  private static Map<String, FunctionGenerator> sFunctionGenerators = new HashMap<String, FunctionGenerator>() {{
    put("random", new RandomNumberGeneratorInPromela());
  }};

  public static final String JOINER_SEQUENCE = ",";
  public static final String EVAL_TEMPLATE = "eval(%s)";
  public static final String FIELD_NAME_TEMPLATE = "field%d";
  public static final String VARIABLE_FIELD_NAME_TEMPLATE = "%s.field%d";
  public static final String FIELD_DECLARATION_TEMPLATE = "%s "+ VARIABLE_FIELD_NAME_TEMPLATE;

    //  typedef Type_1 {
    private static final String DATA_TYPE_START_TEMPLATE = "typedef %s {%n";
    private static final String BLOCK_END_TEMPLATE = "};%n%n";

    //  int x.field1;
    private static final String FIELD_DEFINITION_TEMPLATE = "  %s %s;%n";

    private static final Map<String, String> sDataTypeToPromelaTypeMap = new HashMap<>();
    private static final Map<String, String> sPromelaTypeDefinition = new HashMap<>();
    private static final AtomicInteger sPromelaTypeCounter = new AtomicInteger(1);

    public static String stringToMType(final String pValue) {
    String mtype = pValue;
    mtype = pValue.replaceAll("//s|//.", "_");
    return mtype;
  }

  public static String getMappedType(final String pType) {
    if (BasicType.TYPES[BasicType.NUMBER].equals(pType)) {
      return "byte";
    }
    else if (BasicType.TYPES[BasicType.STRING].equals(pType)) {
      return "mtype";
    }

    return pType;
  }

  public static String dataTypeToVariableDeclaration(final DataType pType, final String pVariable) {
    int counter = 1;
    StringBuilder sb = new StringBuilder();
    for (String type : pType.getTypes()) {
      sb.append(getFieldDeclaration(type, pVariable, counter)).append("; ");
      counter++;
    }
    return sb.toString();
  }

  public static String getFieldDeclaration(final String pType, final String pVariable, final int pFieldPosition) {
    return String.format(FIELD_DECLARATION_TEMPLATE, getMappedType(pType), pVariable, pFieldPosition);
  }

  public static String getFieldName(final String pVariable, final int pFieldPosition) {
    return String.format(VARIABLE_FIELD_NAME_TEMPLATE, pVariable, pFieldPosition);
  }

  public static String dataTypeToReceiveSequence(final DataType pDataType, final String pVariable) {
    AtomicInteger counter = new AtomicInteger(1);
    return pDataType.getTypes().stream()
        .map(s -> String.format(VARIABLE_FIELD_NAME_TEMPLATE, pVariable, counter.getAndIncrement()))
        .collect(Collectors.joining(JOINER_SEQUENCE));
  }

  public static String dataTypeToEvalSequence(final DataType pDataType, final String pVariable) {
    AtomicInteger counter = new AtomicInteger(1);
    return pDataType.getTypes().stream()
        .map(s -> String.format(EVAL_TEMPLATE, getFieldName(pVariable, counter.getAndIncrement())))
        .collect(Collectors.joining(JOINER_SEQUENCE));
  }

  public static String tokenToSendExpression(final Token pToken) {
    return pToken.Tlist.stream().map(bt -> basicTypeToValueAsString(bt)).collect(Collectors.joining(JOINER_SEQUENCE));
  }

  private static String basicTypeToValueAsString(BasicType pBasicType) {
    if (pBasicType.kind == BasicType.NUMBER) {
      return String.format("%d", pBasicType.getValueAsInt());
    }

    return stringToMType(pBasicType.getValueAsString());
  }

  public static String receiveSequenceToEvalSequence(final String pReceiveSequence) {
    return Arrays.asList(pReceiveSequence.split(JOINER_SEQUENCE)).stream()
        .map(s -> String.format(EVAL_TEMPLATE, s))
        .collect(Collectors.joining(JOINER_SEQUENCE));
  }

  public static String receiveSequencesToAssignment(final String pAssignee, final String pAssignment, final int pSpace) {
    String template = "%s";
    if (pSpace > 0) {
      template = String.format("%%%ds", pSpace);
    }
    template = template+"%s = %s;%n";

    StringBuilder sb = new StringBuilder();
    String[] assigneeFields = pAssignee.split(JOINER_SEQUENCE);
    String[] assignmentFields = pAssignment.split(JOINER_SEQUENCE);
    for (int i = 0; i<assigneeFields.length; i++) {
      sb.append(String.format(template, " ", assigneeFields[i], assignmentFields[i]));
    }
    return sb.toString();
  }

  public static String getMultiReceiveStatements(final VariableDefinition pVariableDefinition, final int pSpace) {
    String template = "%s";
    if (pSpace > 0) {
      template = String.format("%%%ds", pSpace);
    }
    template = template + "place_%s??%s;%n";

    StringBuilder sb = new StringBuilder();
    Iterator<String> inputsIterator = pVariableDefinition.getInputPlaceNames().iterator();
    if (inputsIterator.hasNext()) {
      sb.append(String.format(template, "", inputsIterator.next(), pVariableDefinition.getReceiveSequence()));
    }
    while (inputsIterator.hasNext()) {
      sb.append(String.format(template, "", inputsIterator.next(), pVariableDefinition.getEvalSequence()));
    }

    return sb.toString();
  }

  public static String reIndent(final String pString, final int pIndent) {
    String[] lines = pString.split(String.format("%n"));
    String template = String.format("%%%ds%%s%%n",pIndent);
    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      if (StringUtils.isNotBlank(line)) {
        sb.append(String.format(template, "", line));
      }
    }
    return sb.toString();
  }

  public static String dataTypeToPromelaType(final DataType pDataType) {
    String datatypeRep = pDataType.getStringRepresentation();
    String promelaType = sDataTypeToPromelaTypeMap.get(datatypeRep);
    if (promelaType == null) {
      promelaType = String.format("Type_%d", sPromelaTypeCounter.getAndIncrement());
      sDataTypeToPromelaTypeMap.put(datatypeRep, promelaType);
        sPromelaTypeDefinition.put(promelaType, defineDataStructure(promelaType, pDataType));
    }

    return promelaType;
  }

    private static String defineDataStructure(final String pStructureName, final DataType pDataType) {
        StringBuilder sb = new StringBuilder(String.format(DATA_TYPE_START_TEMPLATE, pStructureName));
        int counter = 1;
        for (String type : pDataType.getTypes()) {
            sb.append(String.format(FIELD_DEFINITION_TEMPLATE, getMappedType(type), String.format(FIELD_NAME_TEMPLATE, counter++)));
        }
        sb.append(String.format(BLOCK_END_TEMPLATE));

        return sb.toString();
    }

    public static String getPromelaTypeStructureDefinition(final String pPromelaType) {
        return sPromelaTypeDefinition.get(pPromelaType);
    }

  public static String channelNameForVariable(final String pContext, final String pVariableName) {
    return String.format("set_%s_%s", pContext, pVariableName);
  }

  public static Formula2Promela evaluateFormula(Transition pTransition) {
    String formula = pTransition.getFormula();
    ErrorMsg errorMsg = new ErrorMsg(formula);
    Formula2Promela translator = new Formula2Promela(errorMsg, pTransition, 0);
    translator.setFunctionGeneratorFactory(sFunctionGenerators);
    Sentence s = FormulaUtil.parseFormula(pTransition, errorMsg);
    s.accept(translator);
    return translator;
  }

  public static FunctionGenerator getFunctionGenerator(final String pFunctionName) {
    return sFunctionGenerators.get(pFunctionName);
  }
}
