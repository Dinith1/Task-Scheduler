# SE306.Project1

## Run from IntelliJ
1. Keep .dot files on the top project directory.
2. Change run configuration to include command line inputs to your liking
3. Run Main.main()
4. The output file should be in your top level directory of project

## Build Maven project

1. Run `mvn package`. This should generate a target folder on top directory
2. Open up a terminal and cd to the target folder
3. Run `scheduler.jar`
4. It should run fine, if not, run `mvn clean` and try again.
5. The output file should be in the same directory as your jar. i.e top level directory in target folder

## For IntelliJ or eclipse users

Make sure to enable auto imports, as a maven project might mess with importing otherwise.
