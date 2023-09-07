package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final int MAX_FILE_NAME = 255;
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
   private static final Pattern STRICT_PATH_SEGMENT_CHECK = Pattern.compile("[-._a-z0-9]+");

   public static String findAvailableName(Path pDirPath, String pFileName, String pFileFormat) throws IOException {
      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         pFileName = pFileName.replace(c0, '_');
      }

      pFileName = pFileName.replaceAll("[./\"]", "_");
      if (RESERVED_WINDOWS_FILENAMES.matcher(pFileName).matches()) {
         pFileName = "_" + pFileName + "_";
      }

      Matcher matcher = COPY_COUNTER_PATTERN.matcher(pFileName);
      int j = 0;
      if (matcher.matches()) {
         pFileName = matcher.group("name");
         j = Integer.parseInt(matcher.group("count"));
      }

      if (pFileName.length() > 255 - pFileFormat.length()) {
         pFileName = pFileName.substring(0, 255 - pFileFormat.length());
      }

      while(true) {
         String s = pFileName;
         if (j != 0) {
            String s1 = " (" + j + ")";
            int i = 255 - s1.length();
            if (pFileName.length() > i) {
               s = pFileName.substring(0, i);
            }

            s = s + s1;
         }

         s = s + pFileFormat;
         Path path = pDirPath.resolve(s);

         try {
            Path path1 = Files.createDirectory(path);
            Files.deleteIfExists(path1);
            return pDirPath.relativize(path1).toString();
         } catch (FileAlreadyExistsException filealreadyexistsexception) {
            ++j;
         }
      }
   }

   public static boolean isPathNormalized(Path pPath) {
      Path path = pPath.normalize();
      return path.equals(pPath);
   }

   public static boolean isPathPortable(Path pPath) {
      for(Path path : pPath) {
         if (RESERVED_WINDOWS_FILENAMES.matcher(path.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path createPathToResource(Path pDirPath, String pLocationPath, String pFileFormat) {
      String s = pLocationPath + pFileFormat;
      Path path = Paths.get(s);
      if (path.endsWith(pFileFormat)) {
         throw new InvalidPathException(s, "empty resource name");
      } else {
         return pDirPath.resolve(path);
      }
   }

   public static String getFullResourcePath(String pPath) {
      return FilenameUtils.getFullPath(pPath).replace(File.separator, "/");
   }

   public static String normalizeResourcePath(String pPath) {
      return FilenameUtils.normalize(pPath).replace(File.separator, "/");
   }

   public static DataResult<List<String>> decomposePath(String p_248866_) {
      int i = p_248866_.indexOf(47);
      if (i == -1) {
         DataResult dataresult;
         switch (p_248866_) {
            case "":
            case ".":
            case "..":
               dataresult = DataResult.error(() -> {
                  return "Invalid path '" + p_248866_ + "'";
               });
               break;
            default:
               dataresult = !isValidStrictPathSegment(p_248866_) ? DataResult.error(() -> {
                  return "Invalid path '" + p_248866_ + "'";
               }) : DataResult.success(List.of(p_248866_));
         }

         return dataresult;
      } else {
         List<String> list = new ArrayList<>();
         int j = 0;
         boolean flag = false;

         while(true) {
            String s = p_248866_.substring(j, i);
            switch (p_248866_.substring(j, i)) {
               case "":
               case ".":
               case "..":
                  return DataResult.error(() -> {
                     return "Invalid segment '" + s + "' in path '" + p_248866_ + "'";
                  });
            }

            if (!isValidStrictPathSegment(s)) {
               return DataResult.error(() -> {
                  return "Invalid segment '" + s + "' in path '" + p_248866_ + "'";
               });
            }

            list.add(s);
            if (flag) {
               return DataResult.success(list);
            }

            j = i + 1;
            i = p_248866_.indexOf(47, j);
            if (i == -1) {
               i = p_248866_.length();
               flag = true;
            }
         }
      }
   }

   public static Path resolvePath(Path p_251522_, List<String> p_251495_) {
      int i = p_251495_.size();
      Path path;
      switch (i) {
         case 0:
            path = p_251522_;
            break;
         case 1:
            path = p_251522_.resolve(p_251495_.get(0));
            break;
         default:
            String[] astring = new String[i - 1];

            for(int j = 1; j < i; ++j) {
               astring[j - 1] = p_251495_.get(j);
            }

            path = p_251522_.resolve(p_251522_.getFileSystem().getPath(p_251495_.get(0), astring));
      }

      return path;
   }

   public static boolean isValidStrictPathSegment(String p_249814_) {
      return STRICT_PATH_SEGMENT_CHECK.matcher(p_249814_).matches();
   }

   public static void validatePath(String... p_249502_) {
      if (p_249502_.length == 0) {
         throw new IllegalArgumentException("Path must have at least one element");
      } else {
         for(String s : p_249502_) {
            if (s.equals("..") || s.equals(".") || !isValidStrictPathSegment(s)) {
               throw new IllegalArgumentException("Illegal segment " + s + " in path " + Arrays.toString((Object[])p_249502_));
            }
         }

      }
   }

   public static void createDirectoriesSafe(Path p_259902_) throws IOException {
      Files.createDirectories(Files.exists(p_259902_) ? p_259902_.toRealPath() : p_259902_);
   }
}