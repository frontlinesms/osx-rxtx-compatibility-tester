pushd frontlinesms-at-modem-detector-99b6628
mvn clean install
popd
mvn clean package
cp -r fsms-debugging/ target/
echo 'Project built ::: execute by running fsms-debugging.sh in target dir'