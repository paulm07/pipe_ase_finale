package translation;

import analysis.PromelaUtil;
import formula.FormulaUtil;
import formula.parser.ErrorMsg;
import formula.parser.Formula2Promela;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.texen.util.PropertiesUtil;
import pipe.dataLayer.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class ToJavaProject implements Translator {

    @Override
    public void translate(final DataLayer pModel) throws IOException {
        String modelName = normalizeAsFileName(pModel.pnmlName);
        Path projectPath = ensureDirectories(System.getProperty("user.home"), "PTNet_Projects", modelName);
        Path basePackagePath = ensureDirectories(projectPath.toString(), "src", modelName.toLowerCase());
        Path modelsPackagePath = ensureDirectories(basePackagePath.toString(), "model");
        Path agentsPackagePath = ensureDirectories(basePackagePath.toString(), "agent");

        String modelsPackage = String.format("%s.%s", modelName.toLowerCase(), "model");
        String agentsPackage = String.format("%s.%s", modelName.toLowerCase(), "agent");

        generateModels(pModel, modelsPackage, modelsPackagePath);
        generateAgents(pModel, agentsPackage, agentsPackagePath);
    }

    private void generateAgents(final DataLayer pModel, final String pAgentsPackage, final Path pAgentsPackagePath) throws IOException {
        HashMap<String, List<Transition>> agents = new HashMap<>();
        for (Transition transition : pModel.getTransitions()) {
            String agent = transition.getContainerNet();
            if (StringUtils.isBlank(agent)) {
                agent = "main";
            }

            List<Transition> transitions = agents.get(agent);
            if (transitions == null) {
                transitions = new ArrayList<>();
                agents.put(agent, transitions);
            }
            transitions.add(transition);
        }

        for (String agent : agents.keySet()) {
            generateAgent(agent, agents.get(agent), pAgentsPackage, pAgentsPackagePath);
        }
    }

    private void generateAgent(final String pAgent, final List<Transition> pTransitions, final String pAgentsPackage, final Path pAgentsPackagePath) throws IOException {
//        TODO:: implement this method. create a separate thread class
        StringBuilder importsBuilder = new StringBuilder();
        StringBuilder membersBuilder = new StringBuilder();
        StringBuilder behaviourBuilder = new StringBuilder();

        importsBuilder.append(String.format("package %s;%n", pAgentsPackage));

        importsBuilder.append(String.format("%n%n"));
        membersBuilder.append(String.format("%n%n"));

        for (Transition transition : pTransitions) {
            Formula2Promela translator = PromelaUtil.evaluateFormula(transition);
            behaviourBuilder.append(String.format("public void %s() {%n", transition.getName()))
                .append(String.format("//TODO:: to be implemented%n"))
                .append(String.format("//Pre: %s%n", translator.getPreCondition()));
            String[] lines = translator.getPostCondition().split("((\\r)?\\n)+");
            for (String line : lines) {
                behaviourBuilder.append(String.format("//Post: %s%n", line));
            }
            behaviourBuilder.append(String.format("}%n%n"));
        }

        behaviourBuilder.append(String.format("@Override%n"))
                .append(String.format("public void run() {%n"))
                .append(String.format("//TODO:: to be implemented%n"))
                .append(String.format("}%n%n"));

        String className = StringUtils.capitalize(pAgent);
        File modelFile = new File(pAgentsPackagePath.toFile(), className+".java");
        FileOutputStream fileOutputStream = new FileOutputStream(modelFile);
        fileOutputStream.write(importsBuilder.toString().getBytes());
        fileOutputStream.write(String.format("public class %s extends Thread {%n", className).getBytes());
        fileOutputStream.write(membersBuilder.toString().getBytes());
        fileOutputStream.write(behaviourBuilder.toString().getBytes());
        fileOutputStream.write(String.format("}%n").getBytes());

        fileOutputStream.flush();
        IOUtils.closeQuietly(fileOutputStream);
    }

    private void generateModels(final DataLayer pModel, final String pModelsPackage, final Path pModelsPackagePath) throws IOException {
        File modelPackage = pModelsPackagePath.toFile();
        for (DataType dt : pModel.getDataTypes()) {
            generateModel(dt, pModelsPackage, modelPackage);
        }
    }

    private void generateModel(final DataType pDataType, final String pModelsPackage, final File pModelsPackagePath) throws IOException {
        StringBuilder importsBuilder = new StringBuilder();
        StringBuilder membersBuilder = new StringBuilder();
        StringBuilder propertiesBuilder = new StringBuilder();

        importsBuilder.append(String.format("package %s;%n", pModelsPackage));

        int counter = 1;
        for (String type : pDataType.getTypes()) {
            String fieldName = String.format("field%d", counter++);
            String capitalizeName = StringUtils.capitalize(fieldName);
            String mappedType = getMappedType(type);
            membersBuilder.append(String.format("private %s %s;%n", mappedType, fieldName));
            propertiesBuilder.append(String.format("public %s get%s() {%n", mappedType, capitalizeName))
                    .append(String.format("  return %s;%n", fieldName))
                    .append(String.format("}%n"));
            propertiesBuilder.append(String.format("private void set%s(final %s p%s) {%n", capitalizeName, mappedType, capitalizeName))
                    .append(String.format("  %s = p%s;%n", fieldName, capitalizeName))
                    .append(String.format("}%n"));
        }
        importsBuilder.append(String.format("%n%n"));
        membersBuilder.append(String.format("%n%n"));

        String className = StringUtils.capitalize(normalizeAsFileName(pDataType.getName()));
        File modelFile = new File(pModelsPackagePath, className+".java");
        FileOutputStream fileOutputStream = new FileOutputStream(modelFile);
        fileOutputStream.write(importsBuilder.toString().getBytes());
        fileOutputStream.write(String.format("public class %s {%n", className).getBytes());
        fileOutputStream.write(membersBuilder.toString().getBytes());
        fileOutputStream.write(propertiesBuilder.toString().getBytes());
        fileOutputStream.write(String.format("}%n").getBytes());

        fileOutputStream.flush();
        IOUtils.closeQuietly(fileOutputStream);
    }

    public static String getMappedType(final String pType) {
        if (BasicType.TYPES[BasicType.NUMBER].equals(pType)) {
            return "float";
        }
        else if (BasicType.TYPES[BasicType.STRING].equals(pType)) {
            return "String";
        }

        return pType;
    }

    private String normalizeAsFileName(final String pName) {
        String[] parts = pName.split("[^a-zA-Z0-9]+");
        StringBuilder sb = new StringBuilder();
        if (parts != null && parts.length > 0) {
            for (int i = 0; i<parts.length; i++) {
                sb.append(StringUtils.capitalize(parts[i]));
            }
        }
        else {
            sb.append(pName);
        }

        return sb.toString();
    }

    private Path ensureDirectories(final String pBaseDir, final String... pDirectories) throws IOException {
        Path projectBasePath = Paths.get(pBaseDir, pDirectories);
        if (Files.notExists(projectBasePath, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(projectBasePath);
        }

        return projectBasePath;
    }

    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\Maks\\Documents\\PetriNetExamples\\FiveDiningPhilosophers.xml";
//        DataLayer dataLayer = new DataLayer(new File(path));
//
//        ToJavaProject toJavaProject = new ToJavaProject();
//        try {
//            toJavaProject.translate(dataLayer);
//        } catch (IOException pE) {
//            pE.printStackTrace();
//        }
        System.out.println(File.createTempFile("absdf", "akjdfhkj").getAbsolutePath());
    }
}
