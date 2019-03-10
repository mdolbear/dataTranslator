#MySql
kubectl create -f mysql/mysql-persistent-volume.yml
kubectl create -f mysql/mysql-deployment.yml

#Mongo
kubectl create -f mongo/mongo-persistent-volume.yml
kubectl create -f mongo/mongo-pv-claim.yml
kubectl create -f mongo/mongo-deployment.yml
kubectl create -f mongo/mongo-service.yml
kubectl create -f mongo/mongo-routing.yml

#Mapper
kubectl create -f mapper/mapper-deployment.yml
kubectl create -f mapper/mapper-service.yml
kubectl create -f mapper/mapper-routing.yml

#Ingester
kubectl create -f ingester/ingester-persistent-volume.yml
kubectl create -f ingester/ingester-pv-claim.yml
kubectl create -f ingester/ingester-deployment.yml
kubectl create -f ingester/ingester-service.yml
kubectl create -f ingester/ingester-routing.yml

#K8s provisioning service
kubectl create -f k8provision/k8provision-deployment.yml
kubectl create -f k8provision/k8provision-service.yml
kubectl create -f k8provision/k8provision-routing.yml

