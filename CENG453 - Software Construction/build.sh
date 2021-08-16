cd client
./mvnw clean -Dmaven.test.skip=true package
cd ../server
./mvnw clean -Dmaven.test.skip=true package
cd ..
if [ ! -d "executables" ]
then
	mkdir executables
fi
mv client/target/client14.jar executables/client14.jar
mv server/target/server14.war executables/server14.war
