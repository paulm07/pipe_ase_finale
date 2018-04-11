package generator.code;

import org.apache.commons.lang.StringUtils;
import pipe.dataLayer.DataLayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class AbstractCodeGenerator implements CodeGenerator {

  private final String mType;

  public AbstractCodeGenerator(final String pType) {
    mType = pType;

  }

  protected String normalizeAsFileName(final String pName) {
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

  protected Path ensureDirectories(final String pBaseDir, final String... pDirectories) throws IOException {
    Path projectBasePath = Paths.get(pBaseDir, pDirectories);
    if (Files.notExists(projectBasePath, LinkOption.NOFOLLOW_LINKS)) {
      Files.createDirectories(projectBasePath);
    }

    return projectBasePath;
  }

  protected Path ensurePackageDirectories(final String pPackage, final String pSourceBasePath) throws IOException {
    String[] pathParts = pPackage.split("\\.");
    Path behaviourSourcesBasePath = ensureDirectories(pSourceBasePath, pathParts);
    return behaviourSourcesBasePath;
  }

  @Override
  public final void generate(DataLayer pDataLayer) throws IOException {
    Path path = Paths.get(pDataLayer.pnmlFileName);
    Path projectBasePath = ensureDirectories(path.getParent().toString(), pDataLayer.pnmlName, mType);
    generateImpl(pDataLayer, projectBasePath);
  }

  protected abstract void generateImpl(final DataLayer pDataLayer, final Path pProjectBasePath) throws IOException;
}
