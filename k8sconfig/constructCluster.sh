#Mongo
kubectl create -f mongo/mongo-persistent-volume.yml
kubectl create -f mongo/mongo-pv-claim.yml
kubectl create -f mongo/mongo-deployment.yml
kubectl create -f mongo/mongo-service.yml

#Mapper
kubectl create -f mapper/mapper-deployment.yml
kubectl create -f mapper/mapper-service.yml

#Ingester
kubectl create -f ingester/ingester-persistent-volume.yml
kubectl create -f ingester/ingester-pv-claim.yml
kubectl create -f ingester/ingester-deployment.yml
kubectl create -f ingester/ingester-service.yml

#K8s provisioning service
kubectl create -f k8provision/k8provision-deployment.yml
kubectl create -f k8provision/k8provision-service.yml

#Ingress
kubectl create -f ingress/igctl-default-backend-svc.yml
kubectl create -f ingress/ingress-controller.yml
kubectl create -f ingress/ingress.yml

#Prometheus
kubectl create namespace monitoring
kubectl create -f  prometheus/prom-config-map.yaml
kubectl create -f prometheus/prom-deployment.yaml

