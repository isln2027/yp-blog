 ./mvnw clean package

path_to_tomcat="${1:-../apache-tomcat}"
cp ./target/yp-blog.war ${path_to_tomcat}/webapps/ROOT.war

chmod +x ${path_to_tomcat}/bin/*.sh
echo "Shutdown container"
${path_to_tomcat}/bin/shutdown.sh > /dev/null 2>&1
echo "Starting container"
${path_to_tomcat}/bin/startup.sh
