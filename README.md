# address-validator
Big Data project for my course at the Cooperative State University. Gets, cleans and transforms address data of europe and serves a php frontend for validation.
README only available in german.

## Workflow
Der gesamte Workflow mit dem Job 0_main_job.kjb gestartet, der aus vier weiteren Jobs besteht.
### 1_zip_download_extract:
* Das Zip-Archiv wird heruntergeladen und in einen dafür erstellten Ordner unzippt.

### 2_import_to_hdfs:
* Um die csv-Dateien in das HDFS zu imporieren, wird ein Shellscript generiert.
* Zunächst werden alle mit find gefunden csv-Dateipfade in die Textdatei openaddresses_csv_files.txt gespeichert.
* Nun wird das Java-Tool GenerateImportToHDFS aufgerufen, das für jede csv-Datei einen "hadoop fs -put" Befehl generiert und außerdem die benötigten Verzeichnisse mit "hadoop fs -mkdir" erstellt, sodass die Dateien getrennt nach Land (z.B. "de") abgelegt werden.
* Haben die Verzeichnisse der Länder Unterverzeichnisse, so werden diese aufgelöst und in den Dateinamen integriert, sodass in jedem Verzeichnis ausschließlich Dateien liegen.
* Außerdem wird das Verzeichnis "summary" ausgeschlossen, da hier keine verwertbaren Adressdaten abliegen.
* So wird das Shellscript importToHDFS.sh generiert, ausführbar gemacht und schließlich ausgeführt.
* Die csv-Dateien liegen nun im HDFS unter /user/hadoop/openaddresses/raw.

### 3_hdfs_raw_to_final
* Hier findet der Transfer vom raw- in das final-Verzeichnis statt.
* Zunächst wird eine Transformation (3_create_tables) mit einem SQL-script ausgeführt.
* Dieses erstellt zum einen die external Tabelle openaddr_raw, das alle (auch irrelevante) Spalten der csv-Dateien beinhaltet. Eine Quelle für diese Tabelle wird aber noch nicht eingefügt, also ist sie zunächst leer.
* Außerdem wird die Tabelle openaddr_part erstellt, die nur die 4 relevanten Spalten street, number, postcode und city beinhaltet. Sie wird mit dem Feld country partitioniert und als orc file im final-Verzeichnis gespeichert.
* Danach wird für jedes der 21 Länder eine Transformation (3_part_n_clean) ausgeführt.

## Installation
* PostgreSQL inkl. PHP-Erweiterung muss für das Frontend installiert sein und eine Datenbank main angelegt sein (alles weitere geschieht automatisiert)
* Das kleine Java-Tool GenerateImportToHDFS muss unter /home/hadoop abgelegt werden (erstellt automatisch ein import Shellscript aller csv-Dateien in das HDFS)
* Frontend aus /var/www/html/index.php in das entsprechende Apache-Verzeichnis legen
* Workflow mit /home/hadoop/etl_workflow/0_main_job.kjb starten
