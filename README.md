# Recito
[![Licence GPL](http://img.shields.io/badge/license-GPL-green.svg)](http://www.gnu.org/licenses/quick-guide-gplv3.fr.html)
[![Jenkins](https://img.shields.io/jenkins/build/https/jenkins.qa.ubuntu.com/view%2FPrecise%2Fview%2FAll%2520Precise%2Fjob%2Fprecise-desktop-amd64_default.svg)]()
## Informations importantes
Guide de déploiement de l'application côté back-end.

<b>Attention : </b> Les instances Azure seront révoquées après la présentation de l'application,
 et les différentes API ayant besoin d'un jeton pour fonctionner verront leurs jetons révoqués.
 
 L'application <i><b>Recito</b></i> consiste en un serveur [Spring boot](https://spring.io/) et une base de donnée MongoDB. Par conséquent, il faudra vous assurer d'avoir une base MongoDB 
 disponible et accessible pour réaliser le déploiement de l'application.
 
 Assurez-vous de disposer d'un environnement ayant Java 8, Tomcat et Maven d'installés.
 
 ## Déploiement de l'application
 Pour réaliser le déploiement de notre application, il vous suffit soit de :
 <ul>
 <li>Télécharger le fichier <code>Recito-server.Jar</code> disponible sur ce Git si vous ne souhaitez pas modifier la configuration de notre serveur.</li>
 <li>Cloner cette branche puis de vous placer dans un shell à la racine du projet. Vous pourrez, à ce moment, modifier la configuration du serveur comme décrit dans la section dédiée ci-dessous. Une fois cela réalisé, vous pourrez exécuter la commande <code>$ mvn clean package</code> afin de générer le <code>Recito-server.jar</code>.</li>
 </ul>
 
 Il vous suffira d'exécuter la commande <code>$ java -jar {Répertoire contenant le .jar}/Recito-server.jar</code> pour démarrer le serveur.
 Vous pourrez vérifier le bon fonctionnement de ce dernier en allant sur l'URL <code>http://localhost:8080/</code>. Cette dernière vous retournera un message vous confirmant la bonne mise en place du serveur.
 
 ##Configuration du serveur
 Le serveur dispose d'un fichier de configuration situé à l'emplacement suivant : <code>src/main/resources/application.properties</code>.
 Ce dernier dispose des champs suivants que vous pourrez modifier :
 <ul>
 <li><code>server.port</code> permet de configurer le port de démarrage de l'application, par défaut, ce port est 8080.</li>
 <li><code>spring.data.mongodb.database</code> permet de configurer la connexion vers la base de données correspondate.</li>
 <li><code>spring.data.mongodb.uri</code> permet de configurer la connexion vers l'instance MongoDB hébergeant la base de données via un lien utilisant le protocole <code>mongodb://</code>.</li>
 </ul>
 
 Le jeton pour l'API [Microsoft Speech](https://developer.microsoft.com/fr-fr/windows/speech) et [Microsoft Translator](https://www.microsoft.com/fr-fr/translator/) sont inscrits en dur dans la classe <code>Controleur.java</code> et <code>PDFExtractor.java</code> respectivement.
 
 ##Liste succinte des différents endpoints du serveur 
 Les endpoints suivants sont disponibles :
 <ul>
 <li><code>/</code> permet de vérifier le bon déploiement de l'application.</li>
 <li><code>/getText</code> permet d'obtenir l'objet représentant un texte dans la base de données en fournissant l'id de l'utilisateur et du texte.</li>
 <li><code>/getLibrary</code> permet d'obtenir l'ensemble des textes associés à un utilisateur en fournissant l'id de ce dernier.</li>
 <li><code>/GetProfil</code> permet d'obtenir l'ensemble des objets associés à un utilisateur, dont lui-même, en fournissant l'id de ce dernier.</li>
 <li><code>/signIn</code> permet de s'authentifier sur l'application en échange d'un couple pseudo/mot de passe.</li>
 <li><code>/signOut</code> permet de se déconnecter de l'application.</li>
 <li><code>/createAccount</code> permet de créer un utilisateur dans la base.</li>
 <li><code>/RetrieveFile</code> permet de convertir un fichier <code>.pdf</code> et d'enregistrer son contenu dans la base de données. Il est nécessaire de fournir l'id du client pour utiliser ce dernier.</li>
 <li><code>/RetrieveTextComparison</code> permet de réaliser la comparaison entre le texte prononcé et le texte attendu afin d'obtenir le score associé. Il sera nécesaire de fournir l'id du client et du texte afin de permettre la sauvegarde du score.</li>
 <li><code>/RetrieveSpeechKey</code> permet de récuperer les clefs pour pouvoir utiliser le SDK Microsoft Speech.</li>
 </ul>
 
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
