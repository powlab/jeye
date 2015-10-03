package org.powlab.jeye.tests.thirdparty;

/**
 * Grep prints lines matching a regex.  See printUsageAndExit(...) method to run from command line.
 * This sample shows examples of using next features:
 * 1. Lambda and bulk operations. Working with streams: map(...),filter(...),flatMap(...),limit(...) methods.
 * 2. Static method reference for printing values.
 * 3. New Collections API forEach(...) method.
 * 4. Try-with-resources feature.
 * 5. new Files.walk(...), Files.lines(...) API.
 * 6. Closeable streams.
 */
// TODO java8
//public class Grep {
//
//    private static void printUsageAndExit(String... str) {
//        System.out.println("Usage: " + Grep.class.getSimpleName() + " [OPTION]... PATTERN FILE...");
//        System.out.println("Search for PATTERN in each FILE. If FILE is a directory then whole file tree of the directory will be processed.");
//        System.out.println("Example: grep -m 100 'hello world' menu.h main.c");
//        System.out.println("Options:");
//        System.out.println("    -m NUM: stop analysis after NUM matches");
//        Arrays.asList(str).forEach(System.out::println);
//        System.exit(1);
//    }
//
//    public static void main(String[] args) throws IOException {
//        Pattern pattern;
//        long maxCount = Long.MAX_VALUE;
//        if (args.length < 2) printUsageAndExit();
//        try {
//            int i = 0;
//            //parse OPTIONS
//            while (args[i].startsWith("-")) {
//                switch (args[i]) {
//                    case "-m":
//                        maxCount = Long.parseLong(args[++i]);
//                        break;
//                    default:
//                        printUsageAndExit("Unexpected option " + args[i]);
//                }
//                i++;
//            }
//            //parse PATTERN
//            pattern = Pattern.compile(args[i++]);
//            if (i == args.length) printUsageAndExit("There are no files for input");
//
//            List<Stream<String>> files = Arrays.stream(args, i, args.length)
//                    .map(Paths::get)
//                    .flatMap(Grep::getPathStream)
//                    .filter(Files::isRegularFile)
//                    .map(Grep::path2Lines).collect(Collectors.toList());
//            files.stream().flatMap(file -> file)
//                    .filter(pattern.asPredicate())
//                    .limit(maxCount)
//                    .forEach(System.out::println);
//            files.forEach(Stream::close);
//        } catch (Exception ex) {
//            printUsageAndExit(ex.toString());
//        }
//    }
//
//         * @return Whole file tree starting from path, a stream with one element - the path itself - if it is a file.
//     */
//    private static Stream<Path> getPathStream(Path path) {
//        try (Stream<Path> paths = Files.walk(path)) {
//            return paths.collect(Collectors.<Path>toList()).stream();
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//         * @param path - the file to read
//     */
//
//    private static Stream<String> path2Lines(Path path) {
//        try {
//            return Files.lines(path, StandardCharsets.ISO_8859_1);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//}
