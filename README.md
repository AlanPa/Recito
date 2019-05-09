# Recito
[![Licence GPL](http://img.shields.io/badge/license-GPL-green.svg)](http://www.gnu.org/licenses/quick-guide-gplv3.fr.html)
[![Jenkins](https://img.shields.io/jenkins/build/https/jenkins.qa.ubuntu.com/view%2FPrecise%2Fview%2FAll%2520Precise%2Fjob%2Fprecise-desktop-amd64_default.svg)]()
## Informations importantes
Guide de déploiement de l'application côté front-end.

<b>Attention : </b> Les instances Azure seront révoquées après la présentation de l'application,
 et les différentes API ayant besoin d'un jeton pour fonctionner verront leurs jetons révoqués.
 
 L'application <i><b>Recito</b></i> consiste en un serveur [Spring boot](https://spring.io/) et une base de donnée MongoDB. Par conséquent, il faudra vous assurer d'avoir une base MongoDB 
 disponible et accessible pour réaliser le déploiement de l'application.
 
 Assurez-vous de disposer d'un environnement ayant Java 8, Tomcat et Maven d'installés.
 
 ## Déploiement de l'application
 Pour réaliser le déploiement de notre application, il vous suffit soit de :
 <ul>
 <li>Télécharger le fichier <code>Recito-server-1.0.Jar</code> disponible sur ce Git si vous ne souhaitez pas modifier la configuration de notre serveur.</li>
 <li>Cloner cette branche puis de vous placer dans un shell à la racine du projet. Vous pourrez, à ce moment, modifier la configuration du serveur comme décrit dans la section dédiée ci-dessous. Une fois cela réalisé, vous pourrez exécuter la commande <code>$ mvn clean package</code> afin de générer le <code>Recito-server-1.0.jar</code>.</li>
 </ul>
 
 Il vous suffira d'exécuter la commande <code>$ java -jar {Répertoire contenant le .jar}/Recito-server-1.0.jar</code> pour démarrer le serveur.
 Vous pourrez vérifier le bon fonctionnement de ce dernier en allant sur l'URL <code>http://localhost:8080/</code>. Cette dernière vous retournera un message vous confirmant la bonne mise en place du serveur.
 
 ## Déploiement d'une instance App Services Azure
 Pour réaliser le déploiement sur Azure, il vous faudra disposer d'un compte sur Azure pouvant créer des instances App Services et d'[Azure CLI](https://docs.microsoft.com/fr-fr/cli/azure/index?view=azure-cli-latest) sur votre ordinateur.
 Si l'instance Azure est encore déployée, cette dernière sera accessible [ici](http://recitoback.azurewebsites.net/).
 Ouvrez un shell et utilisez les commandes suivantes :
 * `$ az login` pour vous connecter à Azure.
 * `$ az az ad sp create-for-rbac --name URL --password PASSWORD` pour créer l'instance et obtenir les informations nécessaires. Précisez les valeurs URL et PASSWORD correspondant à l'URL de l'instance et au mot de passe pour vous connecter à cette dernière via FTP si besoin.
 
 Vous receverez en retour le JSON suivant :
 ```
 {
  "appId": "AAAAAAAAAA",
  "displayName": "BBBBBBBBBBB",
  "name": "URL",
  "password": "PASSWORD",
  "tenant": "CCCCCCCCCCCC"
 }
 ```
 
 Ainsi vous pourrez modifier le fichier `settings.xml` situé à la racine du projet (ou de votre configuration maven) de sorte à avoir le contenu suivant :
 ```
    <server>
        <id>azure-auth</id>
        <configuration>
            <client>AAAAAAAAAA</client>
            <tenant>CCCCCCCCCCCC</tenant>
            <key>PASSWORD</key>
            <environment>AZURE</environment>
        </configuration>
    </server>
   ```
 Si vous ne souhaitez pas placer ce fichier de configuration ou modifier celui existant dans votre répertoire d'installation de maven (sous Ubuntu, par exemple, le dossier  `~/.m2`), il vous suffira de rajouter `-s settings.xml` à la commande maven située ci-dessous.
 
 Pour lancer le déploiement sur l'instance App Services précédement créée, il vous suffit de vous placer à la racine du répertoire et d'exécuter la commande `$ mvn clean package azure-webapp:deploy`.
 

 ## Contributions
 
 Seuls les membres de l'hexanôme peuvent commit et push sur le repo git.
 
 ## Licence
 
 [![GNU GPL v3.0](http://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl.html)
 
 ```
 PLD SMART - Une application pour vous aider à apprendre !
 Copyright (C) 2019 Anatolii Gasiuk
 Copyright (C) 2019 Christophe Hirt
 Copyright (C) 2019 Clémentine Coquio-Lebresne
 Copyright (C) 2019 Matthieu Halunka
 Copyright (C) 2019 Tifenn Floch
 Copyright (C) 2019 Alan Paugois
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ```
