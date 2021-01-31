mvn install:install-file -Dfile=$LIB_DIR/geonames-1.1.13.jar -DgroupId=org.geonames -DartifactId=geonames -Dversion=1.1.13 -Dpackaging=jar

mvn clean
mvn install -DskipTests=true

docker build -t spacebel/qa-client:1.0 .