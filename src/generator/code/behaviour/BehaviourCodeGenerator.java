package generator.code.behaviour;

import analysis.PromelaUtil;
import formula.parser.Formula2Promela;
import generator.code.AbstractCodeGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import pipe.dataLayer.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class BehaviourCodeGenerator extends AbstractCodeGenerator{

  private static final String BEHAVIORS = "bPlaces";
  private static final String BASE_PACKAGE = "bPackage";
  private static final String SOURCE_BASE = "sbPath";
  private static final String BEHAVIOR_PACKAGE = "behaviorPackage";
  private static final String MODEL_PACKAGE = "modelPackage";
  private static final Object BEHAVIOR_CLASSES = "bClasses";

  public BehaviourCodeGenerator() {
    super("behavior");
  }

  private List<Place> findBehaviours(final DataLayer pDataLayer) {
    List<Place> places = Arrays.asList(pDataLayer.getPlaces()).stream().filter(p -> p.getDataType().getName().startsWith("behavior_"))
        .collect(Collectors.toList());
    return places;
  }

  private List<String> findBehaviourClassNames(final List<Place> pBehaviourPlaces) {
    return pBehaviourPlaces.stream()
        .map(p -> String.format("%sBehaviour", StringUtils.capitalize(p.getName())))
        .collect(Collectors.toList());
  }

  @Override
  protected void generateImpl(DataLayer pDataLayer, Path pProjectBasePath) throws IOException {
    String modelName = pDataLayer.pnmlName;
    String basePackage = modelName.toLowerCase();

    Path sourceBasePath = ensureDirectories(pProjectBasePath.toString(), "src", "main", "java");
    List<Place> behaviourPlaces = findBehaviours(pDataLayer);
    if (behaviourPlaces == null || behaviourPlaces.isEmpty()) {
      System.out.println("There is no places representing behavior. Returning");
      return;
    }

    List<String> behaviourClasses = findBehaviourClassNames(behaviourPlaces);

    Properties baseContext = new Properties();
    baseContext.put(BEHAVIORS, behaviourPlaces);
    baseContext.put(BEHAVIOR_CLASSES, behaviourClasses);
    baseContext.put(BASE_PACKAGE, basePackage);
    baseContext.put(BEHAVIOR_PACKAGE, String.format("%s.behavior", basePackage));
    baseContext.put(MODEL_PACKAGE, String.format("%s.model", basePackage));
    baseContext.put(SOURCE_BASE, sourceBasePath.toString());

    Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    Velocity.init();

    generateModelClasses(pDataLayer.getDataTypes(), baseContext);
    generateBehaviourClasses(baseContext);
    generateMainClass(baseContext);
  }

  private void generateModelClasses(final Collection<DataType> pDataTypes, final Properties pBaseContext) throws IOException {
    String modelPackage = String.format("%s.%s", pBaseContext.get(BASE_PACKAGE), "model");
    Path modelSourcesBasePath = ensurePackageDirectories(modelPackage, pBaseContext.getProperty(SOURCE_BASE));

    for (DataType dt : pDataTypes) {
      String modelName = normalizeAsFileName(dt.getName());
      Map<String, String> fieldsMap = new HashMap<>();
      StringBuilder sb = new StringBuilder();
      int counter = 1;
      for (String type : dt.getTypes()) {
        String key = String.format("Field%d", counter++);
        String value = type.equals(BasicType.TYPES[BasicType.NUMBER])? "float" : "String";
        fieldsMap.put(key, value);
        sb.append(String.format("final %s p%s, ", value, key));
      }

      if (sb.length() >= 2) {
        sb.setLength(sb.length()-2);
      }

      VelocityContext context = new VelocityContext();
      context.put("package", modelPackage);
      context.put("model", modelName);
      context.put("fields", fieldsMap.entrySet());
      context.put("constructorParams", sb.toString());

      writeTemplate(modelSourcesBasePath.toString(), modelName, "/template/velocity/behavior/model.vm", context);
    }
  }

  private void writeTemplate(final String pBasePath, final String pModelName, final String pTemplate, final VelocityContext pContext) throws IOException {
    Path modelClassPath = Paths.get(pBasePath, String.format("%s.java", pModelName));
    FileWriter writer = new FileWriter(modelClassPath.toString(), false);

    Template template = Velocity.getTemplate(pTemplate);
    template.merge(pContext, writer);
    writer.flush();
    IOUtils.closeQuietly(writer);
  }

  private void generateMainClass(final Properties pBaseContext) throws IOException {
    String mainPackage = String.format("%s.%s", pBaseContext.get(BASE_PACKAGE), "main");
    Path mainSourcesBasePath = ensurePackageDirectories(mainPackage, pBaseContext.getProperty(SOURCE_BASE));

    List<String> behaviorClasses = (List<String>)pBaseContext.get(BEHAVIOR_CLASSES);
    String behaviorPackage = (String) pBaseContext.get(BEHAVIOR_PACKAGE);
    List<String> localImports = behaviorClasses.stream().map(s -> String.format("%s.%s", behaviorPackage, s)).collect(Collectors.toList());

    VelocityContext context = new VelocityContext();
    context.put("package", mainPackage);
    context.put("imports", localImports);
    context.put("behaviorClasses", behaviorClasses);

    writeTemplate(mainSourcesBasePath.toString(), "Main", "/template/velocity/behavior/main.vm", context);
  }

  private void generateBehaviourClasses(final Properties pBaseContext) throws IOException {
    String behaviourPackage = (String) pBaseContext.get(BEHAVIOR_PACKAGE);
    Path behaviourSourcesBasePath = ensurePackageDirectories(behaviourPackage, pBaseContext.getProperty(SOURCE_BASE));

    List<Place> places = (List<Place>) pBaseContext.get(BEHAVIORS);
    for (Place place : places) {
      String behaviourName = StringUtils.capitalize(place.getName());
      String behaviourClassName = String.format("%sBehaviour", behaviourName);

      Transition takeControl = place.getArcInList().get(0).getTransition();
      Formula2Promela translator = PromelaUtil.evaluateFormula(takeControl);

      VelocityContext context = new VelocityContext();
      context.put("package", behaviourPackage);
      context.put("behavior", behaviourClassName);
      context.put("precondition", translator.getPreCondition());
      context.put("modifiedPre", translator.getPreCondition());
      context.put("postConditions", Arrays.asList(translator.getPostCondition().split(String.format("%n"))));

      writeTemplate(behaviourSourcesBasePath.toString(), behaviourClassName, "/template/velocity/behavior/behavior.vm", context);

//      writer.write(String.format("package %s;%n%n", behaviourPackage));
//      writer.write(String.format("%s%n", "import lejos.nxt.*;"));
//      writer.write(String.format("%s%n%n%n", "import lejos.robotics.subsumption.*;"));
//      writer.write(String.format("public class %s implements Behaviour {%n", behaviourClassName));
//      writer.write(String.format("  private AtomicBoolean isSuppressed = new AtomicBoolean(false);%n%n"));
//      writeTakingControlTemplate(translator.getPreCondition(), writer);
//      writeSuppressTemplate(writer);
//      writeActionTemplate(translator.getPostCondition(), writer);
//      writer.flush();
//      IOUtils.closeQuietly(writer);
    }
  }

  private void writeActionTemplate(final String postCondition, final FileWriter pWriter) throws IOException {
    pWriter.write(String.format("  @Override%n"));
    pWriter.write(String.format("  public void action() {%n"));
    pWriter.write(String.format("    isSuppressed.getAndSet(false);%n"));
    pWriter.write(String.format("    //TODO:: implement the post condition;%n"));
    pWriter.write(String.format("    //Fix-Me:: PostConditions %n"));
    for (String line : postCondition.split(String.format("%n"))) {
      pWriter.write(String.format("    // %s%n", line));
    }
    pWriter.write(String.format("  }%n%n%n"));
  }

  private void writeSuppressTemplate(final FileWriter pWriter) throws IOException {
    AtomicBoolean atomicBoolean = new AtomicBoolean();
    atomicBoolean.getAndSet(false);
    pWriter.write(String.format("  @Override%n"));
    pWriter.write(String.format("  public boolean suppress() {%n"));
    pWriter.write(String.format("    isSuppressed.getAndSet(true);%n"));
    pWriter.write(String.format("  }%n%n%n"));
  }

  private void writeTakingControlTemplate(final String pCondition, final FileWriter pWriter) throws IOException {
    pWriter.write(String.format("  @Override%n"));
    pWriter.write(String.format("  public boolean takeControl() {%n"));
    pWriter.write(String.format("    boolean shouldControl = false;%n"));
    pWriter.write(String.format("    //TODO:: evaluate precondition %n"));
    pWriter.write(String.format("    //Fix-me:: %s%n", pCondition));
    pWriter.write(String.format("    return shouldControl;%n"));
    pWriter.write(String.format("  }%n%n%n"));
  }

  public static void main(String[] args) throws IOException {
    String path = "C:\\Users\\Maks\\Documents\\PetriNetExamples\\Behaviour\\CarParkingSystem.xml";
    DataLayer dataLayer = new DataLayer(new File(path));
    BehaviourCodeGenerator generator = new BehaviourCodeGenerator();
    generator.generate(dataLayer);
  }
}
