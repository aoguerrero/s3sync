# s3sync

## Descripción

Esta utilidad sincroniza los archivos de un directorio con un bucket de Amazon S3, no soporta subdirectorios.

Los archivos en la nube son nombrados codificando el nombre original en Base64 para evitar problemas con nombres de archivo que contengan caracteres especiales.

Requiere JDK 1.7+ y Apache Maven.

## Uso

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
encodeNames=
removeFiles=
```

Debe llenar los valores de cada llave

|Llave|Descripción|
|-----|-----------|
|accessKey|Valor de autenticación del usuario de AWS|
|secretKey|Valor de autenticación del usuario de AWS|
|region|Region de AWS donde se encuentra el bucket, por ejemplo us-east-1|
|bucket|Identificador del bucket|
|exclude|Archivos que deben excluirse en la sincronización separados por coma|
|download|Indica si se desean descargar los archivos que se encuentran en S3 que no están localmente, los posibles valores on true o false|
|encodeNames|Indica si se conservarán los nombres originales de los archivos o se codificarán, los posibles valores son true o false|
|removeFiles|Indica si se deben eliminar los archivos una vez cargados, los posibles valores son true o false|


4) Ejecute el archivo Jar desde el directorio donde se encuentran los archivos

```
cd /destination/path
java -jar s3sync.jar <opciones>
```

*Las opciones disponibles son:*

```
--home=/rutal/directorio
```
Indica que en lugar de buscar la configuración y los archivos a cargar / descargar en el directorio donde actual se inicie en esa ruta.


## TO-DO

* Soporte a subdirectorios
* Soporte para Google Cloud
