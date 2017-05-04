#!/bin/sh

echo "Copia DFS"
cp -r dfs/dfs/*.java dfs/dfs/*.sh dfs;

echo "Compila DFS"
cd dfs; 
./compila_y_construye_JARS.sh;


echo "Servidor"
cd servidor
./compila_servidor.sh
./arranca_rmiregistry 33333 &
./ejecuta_servidor.sh 33333


echo "Cliente"
cd cliente; 
./compila_programa.sh Test.java
SERVIDOR=localhost PUERTO=33333 ./ejecuta_programa.sh Test 16 10


echo "Remove DFS"
rm  dfs/*.java dfs/*.sh dfs/*.jar dfs/*.class

dfs/DFSCliente.java dfs/DFSFicheroCliente.java dfs/DFSFicheroServ.java dfs/DFSFicheroServImpl.java dfs/DFSServicio.java dfs/DFSServicioImpl.java dfs/FicheroInfo.java dfs/DFSFicheroCallbackImpl.java dfs/DFSFicheroCallback.java
