# s3sync

Esta utilidad sincroniza los archivos de un directorio con un bucket de Amazon S3, no soporta subdirectorios.

Uso:

1) Genere el Jar ejecutable

```
mvn clean package
```

2) Copie el Jar en el directorio que contiene los archivos que desea sincronizar

```
cp target/s3sync.jar /destination/path
```    
   
3) En el directorio `/destination/path` agregue un archivo llamado `s3sync.properties` con el siguiente contenido

```
accessKey=
secretKey=
region=
bucket=
exclude=s3sync.jar,s3sync.properties
download=
```

Debe llenar los valores de cada llave

|Llave|Descripción|
|-----|-----------|
|accessKey|Valor de autenticación del usuario de AWS|
|secretKey|Valor de autenticación del usuario de AWS|
|region|Region de AWS donde se encuentra el bucket|
|bucket|Identificador del bucket|
|exclude|Archivos que deben excluirse en la sincronización|
|download|Indica si se desean descargar los archivos que se encuentran en S3 que no están localmente, los posibles valores on true o false|


4) Ejecute el archivo Jar desde el directorio donde se encuentran los archivos

```
cd /destination/path
java -jar s3sync.jar
```
