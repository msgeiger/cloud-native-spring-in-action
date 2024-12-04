docker exec -it simplyadd-keycloak bash

cd /opt/keycloak/bin

./kcadm.sh config credentials \
   --server http://localhost:8080 \
   --realm master \
   --user user \
   --password password

./kcadm.sh create realms -s realm=SimplyAdd -s enabled=true

./kcadm.sh create roles -r SimplyAdd -s name=employee

./kcadm.sh create roles -r SimplyAdd -s name=customer

./kcadm.sh create users -r SimplyAdd \
    -s username=issabelle \
    -s firstName=Issabelle \
    -s lastName=Dahl \
    -s enabled=true

    -- test1@test1.com

./kcadm.sh add-roles -r SimplyAdd \
    --uusername issabelle \
    --rolename employee \
    --rolename customer

./kcadm.sh create users -r SimplyAdd \
    -s username=bjorne \
    -s firstName=Bjorne \
    -s lastName=Vinterberg \
    -s enabled=true

./kcadm.sh add-roles -r SimplyAdd \
    --uusername bjorne \
    --rolename customer

./kcadm.sh set-password -r SimplyAdd \
   --username issabelle --new-password password

./kcadm.sh set-password -r SimplyAdd \
   --username bjorne --new-password password

./kcadm.sh create clients -r SimplyAdd \
   -s clientId=edge-service \
   -s enabled=true \
   -s publicClient=false \
   -s secret=simplyadd-keycloak-secret \
   -s 'redirectUris=["http://localhost:9000",
"http://localhost:9000/login/oauth2/code/*"]'
