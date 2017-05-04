#!/bin/sh

rm -f ./JavaDFS.2016.tar.gz
cp -r dfs/dfs/*.java dfs/dfs/*.sh dfs;
tar -cvzf JavaDFS.2016.tar.gz dfs/DFSCliente.java dfs/DFSFicheroCliente.java dfs/DFSFicheroServ.java dfs/DFSFicheroServImpl.java dfs/DFSServicio.java dfs/DFSServicioImpl.java dfs/FicheroInfo.java dfs/DFSFicheroCallbackImpl.java dfs/DFSFicheroCallback.java autores memoria.txt
rm  dfs/*.java dfs/*.sh dfs/*.jar dfs/*.class
rsync -avz JavaDFS.2016.tar.gz decompress.sh y16a042@triqui4.fi.upm.es:/homefi/alumnos/y/y16a042/DATSI/SD/JavaDFS.2016
