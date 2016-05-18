Proiect Instrumente pentru Dezvoltarea Programelor (Chat cu integrare Facebook)

Pentru dezvoltarea proiectului am folosit conform cerintei o arhitectura de tip client-server. Am ales sa folosesc pentru transmiterea de mesaje intre client si server api-ul RMI.(https://docs.oracle.com/javase/tutorial/rmi/). Pentru design si interfata am folosit Swing iar pentru logarea cu facebook am folosit Java FX iar pentru integrarea java-facebook am folosit facebook4j (http://facebook4j.org/en/index.html) , si restFB (http://restfb.com/) framework-uri ce vin ca un wrapper peste serviciile REST oferite de facebook prin aplicatia GRAPH. Serverul tine datele si fisierele stocate intr-o baza de date MySQL. Tot acolo administreaza grupurile si persista conversatiile.

Cerinte : 
1. Logarea cu facebook.
  Pentru logarea cu facebook am folosit Java FX prin obiectul WebEngine ce deschide url-ul de logare la facebook alaturi de permisiunile necesare aplicatiei. Am atasat de frame-ul ce contine webengine-ul un worker ce verifica pe schimbarea site-ului , parametrii din url. Daca logarea este corecta, worker-ul extrage din url codul de autentificare si creaza un token cu acesta folosit mai departe pentru constructia obiectului Facebook din api-ul facebook4j pe partea de server. Cu ajutorul acestui api extrag numele utilizatorului, lista cu grupurile din care acesta face parte si lista prietenilor ce folosesc aplicatia. In server retin intr-un hashmap interfata clientului logat si obiectul Facebook avand ca cheie username-ul acestuia. Pe baza raspunsului de la server, [opulez JTree-ul cu aceste date si utilizatorul poate sa folosesasca aplicatia.
2. Chat-ul
  Utilizatorul dupa ce este autentificat poate incepe sa comunice in modul grup sau modul simplu. Serverul mediaza comunicarile in functie de hash-map-urile retinute in memorie.
3. File transfer-ul
  Utilizatorul are posibilitatea sa transfere fisiere prin apasarea butonului "Adauga fisier". Dupa ce selecteaza fisierul , pe apasarea butonului de send sau a tastei enter fisierul este incarcat pe server. Daca destinatarul doreste sa descarce fisierul , serverul intai verifica daca utilizatorul este online , daca da , serverul intoarce destinatarului interfata clientului ce a facut upload pentru a trasnfera fisierul client-to-client, in caz contrar, serverul va raspunde cu fisierul cerut, fisier retinut in baza de date sub forma unui LONGBLOB.
4. Mesaje de control
  Pentru control am preferat sa folosesc mesajele de control decat sa supraincarc interfata grafica. 

/groupRequests -> afiseaza utilizatorului o lista cu cererile de intrare in grup din grupul selectat

/acceptRequest [requester name] -> accepta cererea utilizatorului de a intra in grupul selectat

/joinGroup -> trimite o cerere de acceptare utilizatorilor logati in grupul selectat si serverul o retine

/leaveGroup -> paraseste grupul selectat

/files -> afiseaza utilizatorului o lista cu fisierele partajate intre cei doi interlocutori (doar client-to-client)

/groupFiles -> afiseaza o lista cu fisierele partajate in cadrul grupului

/getFile [file id] -> preia fisierul cu id-ul dat

/postStatusMessage [message] -> posteaza mesajul [message] pe facebook
