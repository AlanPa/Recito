# Recito
[![Licence GPL](http://img.shields.io/badge/license-GPL-green.svg)](http://www.gnu.org/licenses/quick-guide-gplv3.fr.html)
[![Jenkins](https://img.shields.io/jenkins/build/https/jenkins.qa.ubuntu.com/view%2FPrecise%2Fview%2FAll%2520Precise%2Fjob%2Fprecise-desktop-amd64_default.svg)]()
## Informations importantes
Guide de déploiement de l'application côté front-end.

<b>Attention : </b> Les instances Azure seront révoquées après la présentation de l'application,
 et les différentes API ayant besoin d'un jeton pour fonctionner verront leurs jetons révoqués.
 
 <b>Attention bis : </b> Il faut déployer le back-end d'abord.
  
 Assurez-vous de disposer d'un environnement ayant Java 8, Gradle et Android Studio d'installés sur votre ordinateur si vous souhaitez régénérer l'`APK`.
 
L'application fonctionne avec une connexion internet obligatoire durant l'intégralité de l'usage de cette dernière.
Les `.pdf` ont pour l'instant une limitation : ils ne doivent comporter que du texte pour pouvoir être lu correctement.
 
## Déploiement de l'application
 Avant d'effectuer le deployement de la partie `front-end`, il est indispensable de deployer au préalable la partie `back-end`.
 Il y a deux façons de déployer l'application :
 
### Méthode 1
 
 Télécharger le fichier `.apk`, qui se trouve à la racine de la branche `master-front`, directement sur votre téléphone. Puis exécuter le fichier en acceptant l'installation d'une application en désactivant les services de sécurités proposées.

### Méthode 2
  
  Télécharger la branche `master-front` sur votre ordinateur et ouvrir le projet dans Android Studio. Il vous suffira ensuite de `run`
  l'application sur un des émulateurs d'Android Studio ou sur votre téléphone en activant le mode `developpeur` au préalable.
 
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
