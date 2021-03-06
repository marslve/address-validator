# address-validator
Big Data project for my course at the Cooperative State University. Gets, cleans and transforms address data of europe and serves a php frontend for validation.
README only available in german.

## Workflow
Der gesamte Workflow mit dem Job *0_main_job.kjb* gestartet, der aus vier weiteren Jobs besteht.

### 1_zip_download_extract:
* Das Zip-Archiv wird heruntergeladen und in einen dafür erstellten Ordner unzippt.

### 2_import_to_hdfs:
* Um die csv-Dateien in das HDFS zu imporieren, wird ein Shellskript generiert.
* Zunächst werden alle mit find gefunden csv-Dateipfade in die Textdatei openaddresses_csv_files.txt gespeichert.
* Nun wird das Java-Tool *GenerateImportToHDFS* aufgerufen, das für jede csv-Datei einen *hadoop fs -put* Befehl generiert und außerdem die benötigten Verzeichnisse mit *hadoop fs -mkdir* erstellt, sodass die Dateien getrennt nach Land (z.B. "de") abgelegt werden.
* Haben die Verzeichnisse der Länder Unterverzeichnisse, so werden diese aufgelöst und in den Dateinamen integriert, sodass in jedem Verzeichnis ausschließlich Dateien liegen.
* Außerdem wird das Verzeichnis *summary* ausgeschlossen, da hier keine verwertbaren Adressdaten abliegen.
* So wird das Shellscript importToHDFS.sh generiert, ausführbar gemacht und schließlich ausgeführt.
* Die csv-Dateien liegen nun im HDFS unter */user/hadoop/openaddresses/raw*.

### 3_hdfs_raw_to_final
* Hier findet der Transfer vom raw- in das final-Verzeichnis statt.
* Zunächst wird eine Transformation (**3_create_tables**) mit einem SQL-script ausgeführt.
* Dieses erstellt zum einen die external Tabelle *openaddr_raw*, das alle (auch irrelevante) Spalten der csv-Dateien beinhaltet. Eine Quelle für diese Tabelle wird aber noch nicht eingefügt, also ist sie zunächst leer.
* Außerdem wird die Tabelle *openaddr_part* erstellt, die nur die 4 relevanten Spalten *street*, *number*, *postcode* und *city* beinhaltet. Sie wird mit dem Feld *country* partitioniert und als orc file im *final*-Verzeichnis gespeichert.
* Danach wird für jedes der 21 Länder eine Transformation (**3_part_n_clean**) ausgeführt, welche den entsprechenden Ländercode über den Parameter *cntry* erhält (z.B. "de").
* Hierbei wird ein SQL-Script ausgeführt, dass der Tabelle *openaddr_raw* die richtige *LOCATION* der Daten zuweist, sodass sie immer nur die Rohdaten eines einzigen Landes beinhaltet. Die Daten der relevanten Spalten werden an die partitionierte Tabelle *openaddr_part* übertragen, wobei doppelte Einträge und Einträge mit leeren Feldern aussortiert werden.
* Wurden die Transformationen für jedes Land ausgeführt, so liegen die partionierten, gecleanten Daten im *final*-Verzeichnis.

### 4_hive_to_postgres
* Hier werden die final-Daten aus dem HDFS in das end-user DMBS (PostgreSQL) übertragen.
* Die erste Tranformation (**4_create_postgresql_table**) führt ein SQL-Skript aus, die die benötigte Tabelle in der PostgreSQL-Datenbank erstellt.
* Die zweite Transformation (**4_hive_to_postgres**) besteht aus einem Table Input und Output. Beim Input werden die Tabellendaten vom Hiveserver geholt und mithilfe des Outputs in die PostgreSQL-Tabelle *openaddr* übertragen.

## Installation
* PostgreSQL inkl. PHP-Erweiterung muss für das Frontend installiert sein und eine Datenbank *main* angelegt sein (alles weitere geschieht automatisiert, daher **keine DDLs nötig!**)
* Das kleine Java-Tool GenerateImportToHDFS muss unter /home/hadoop abgelegt werden (erstellt automatisch ein Import-Shellskript aller csv-Dateien in das HDFS)
* Frontend aus */var/www/html/index.php* in das entsprechende Apache-Verzeichnis legen
* Main-job des Workflows */home/hadoop/etl_workflow/0_main_job.kjb* starten
