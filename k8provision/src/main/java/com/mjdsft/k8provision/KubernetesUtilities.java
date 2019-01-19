package com.mjdsft.k8provision;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KubernetesUtilities {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private CoreV1Api apiObject;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private boolean insideCluster;

    //Constants
    public static final String DEFAULT_NAME_SPACE = "default";
    private static final Integer TIME_OUT_VALUE = 180;

    /**
     * Answer a default instance
     * @param isInsideCluster boolean
     */
    public KubernetesUtilities(boolean isInsideCluster) {

        super();
        this.setInsideCluster(isInsideCluster);

    }

    /**
     * Initialize client based on policy
     */
    public void initializeClient() throws IOException {

        ApiClient tempClient;

        tempClient = this.getProperClientType();
        Configuration.setDefaultApiClient(tempClient);
        this.setApiObject(new CoreV1Api(tempClient));

    }

    /**
     * Answer correct client type
     * @return APiClient
     */
    private ApiClient getProperClientType() throws IOException {

        ApiClient   tempResult;

        if (this.isInsideCluster()) {

            tempResult = Config.fromCluster();
        }
        else {

            tempResult = Config.defaultClient();
        }

        return tempResult;

    }


    /**
     * Answer a list of services for aNamespace
     * @param aNamespace String
     * @return List
     * @throws ApiException
     */
    public List<V1Service> getServices(String aNamespace) throws ApiException {

        List<V1Service> tempServices;
        V1ServiceList   tempServiceList;

            tempServiceList =
                this.getApiObject().listNamespacedService(aNamespace,
                                                   null,
                                                 null,
                                              null,
                                         null,
                                              null,
                                                          Integer.MAX_VALUE,
                                            null,
                                                          TIME_OUT_VALUE,
                                                          Boolean.FALSE);
        return tempServiceList.getItems();

    }

    /**
     * Answer my service names for aServices
     * @param aServices List
     * @return List
     */
    public List<String> asServiceNames(List<V1Service> aServices) {

        return aServices
                .stream()
                .map(v1service -> v1service.getMetadata().getName())
                .collect(Collectors.toList());
    }

    /**
     * All all namespaces in the cluster
     *
     * @return
     * @throws ApiException
     */
    public List<V1Namespace> getAllNameSpaces() throws ApiException {


        V1NamespaceList tempNamespaceList;

        this.validateCoreApiObjectExists();
        tempNamespaceList =
                this.getApiObject().listNamespace(null,
                                               null,
                                             null,
                                        null,
                                             null,
                                                   0,
                                          null,
                                                        Integer.MAX_VALUE,
                                                        Boolean.FALSE);
        return
                tempNamespaceList.getItems();


    }

    /**
     * Answer the names for aNamespaces
     * @param aNamespaces List
     * @return List
     */
    public List<String> asNamespaceNames(List<V1Namespace> aNamespaces) {

        return aNamespaces.stream()
                          .map(v1Namespace -> v1Namespace.getMetadata().getName())
                          .collect(Collectors.toList());
    }

    /**
     * Answer all pods for the cluster
     *
     * @return List
     * @throws ApiException
     */
    public List<V1Pod> getPods() throws ApiException {

        V1PodList v1podList;

        this.validateCoreApiObjectExists();
        v1podList =
                this.getApiObject().listPodForAllNamespaces(null,
                                                          null,
                                                     null,
                                                          null,
                                                                null,
                                                               null,
                                                        null,
                                                        null,
                                                               null);

        return v1podList.getItems();

    }

    /**
     * Answer the pod names for aPods
     * @param aPods List
     * @return List
     */
    public List<String> asPodNames(List<V1Pod> aPods) {

        return aPods.stream()
                    .map(v1Pod -> v1Pod.getMetadata().getName())
                    .collect(Collectors.toList());

    }

    /**
     * Answer all pods for the default namespace
     *
     * @return List
     * @throws ApiException
     */
    public List<String> getNamespacedPod() throws ApiException {

        return this.asPodNames(this.getNamespacedPod(DEFAULT_NAME_SPACE, null));
    }

    /**
     * Answer all pod names for the specified namespace
     *
     * @param namespace
     * @return List
     * @throws ApiException
     */
    public List<String> getNamespacedPod(String namespace) throws ApiException {

        return this.asPodNames(this.getNamespacedPod(namespace, null));
    }

    /**
     * Answer pods for a specific aNamespace and aLabel
     *
     * @param aNamespace
     * @param aLabel
     * @return List
     * @throws ApiException
     */
    public List<V1Pod> getNamespacedPod(String aNamespace,
                                         String aLabel) throws ApiException {
        V1PodList tempPodList;

        this.validateCoreApiObjectExists();
        tempPodList =
                this.getApiObject().listNamespacedPod(aNamespace,
                                               null,
                                             null,
                                          null,
                                     null,
                                                      aLabel,
                                                      Integer.MAX_VALUE,
                                        null,
                                                      TIME_OUT_VALUE,
                                                      Boolean.FALSE);
        return tempPodList.getItems();

    }

    /**
     * Answer the log contents for aPodName in aNamespace
     *
     * @param aNamespace String
     * @param aPodName String
     * @throws ApiException
     */
    public String readLogForPod(String aNamespace,
                                String aPodName) throws ApiException {

        this.validateCoreApiObjectExists();
        return
                this.getApiObject().readNamespacedPodLog(aPodName,
                                                        aNamespace,
                                               null,
                                                        Boolean.FALSE,
                                                        Integer.MAX_VALUE,
                                                 null,
                                                        Boolean.FALSE,
                                                        Integer.MAX_VALUE,
                                               40,
                                                        Boolean.FALSE);

    }

    /**
     * Scale up/down the number of pods in a Deployment
     * @param aNamespace String
     * @param deploymentName String
     * @param numberOfReplicas int
     * @throws ApiException
     */
    public void scaleDeployment(@NonNull String aNamespace,
                                @NonNull String deploymentName,
                                int numberOfReplicas)
            throws ApiException {

        List<ExtensionsV1beta1Deployment>       extensionsV1beta1DeploymentItems;
        ExtensionsV1beta1Api                    extensionV1Api;
        Optional<ExtensionsV1beta1Deployment>   tempDeployment;

        extensionV1Api = this.getExtensionsV1beta1Api();
        tempDeployment = this.getDeployment(aNamespace, deploymentName, extensionV1Api);
        this.scaleDeployment(aNamespace, deploymentName, numberOfReplicas, extensionV1Api, tempDeployment);

    }

    /**
     * Scale deployment
     * @param aNamespace String
     * @param deploymentName String
     * @param aNumberOfReplicas int
     * @param anExtensionV1Api anExtensionV1Api
     * @param aDeployment D
     */
    private void scaleDeployment(String aNamespace,
                                 String deploymentName,
                                 int aNumberOfReplicas,
                                 ExtensionsV1beta1Api anExtensionV1Api,
                                 Optional<ExtensionsV1beta1Deployment> aDeployment) {

        aDeployment.ifPresent(

                (ExtensionsV1beta1Deployment deploy) -> {

                    ExtensionsV1beta1DeploymentSpec newSpec;
                    ExtensionsV1beta1Deployment newDeploy;

                    try {
                        newSpec = deploy.getSpec().replicas(aNumberOfReplicas);
                        newDeploy = deploy.spec(newSpec);
                        anExtensionV1Api.replaceNamespacedDeployment(deploymentName,
                                                                   aNamespace,
                                                                   newDeploy,
                                                            null);

                    }
                    catch (ApiException ex) {

                        throw new RuntimeException("Scaling the pod failed for namespace:"
                                                                        + aNamespace
                                                                        + " deployment:"
                                                                        + deploymentName, ex);
                    }

                }

            );

    }

    /**
     * Answer a deployment for the following arguments
     * @param aNamespace String
     * @param deploymentName String
     * @param extensionV1Api ExtensionsV1beta1Api
     * @return Optional<ExtensionsV1beta1Deployment>
     * @throws ApiException
     */
    private Optional<ExtensionsV1beta1Deployment> getDeployment(String aNamespace,
                                                                String deploymentName,
                                                                ExtensionsV1beta1Api extensionV1Api)
                            throws ApiException {

        List<ExtensionsV1beta1Deployment>       tempDeploymentItems;
        Optional<ExtensionsV1beta1Deployment>   tempDeployment;

        tempDeploymentItems = this.getDeploymentsForNamespace(aNamespace, extensionV1Api);
        tempDeployment =
                tempDeploymentItems
                        .stream()
                        .filter(
                                (ExtensionsV1beta1Deployment deployment) ->
                                        deployment.getMetadata().getName().equals(deploymentName))
                        .findFirst();

        return tempDeployment;

    }

    /**
     * Answer the deplouyments for aNamespace
     * @param aNamespace String
     * @param extensionV1Api ExtensionsV1beta1Api
     * @return
     * @throws ApiException
     */
    private List<ExtensionsV1beta1Deployment> getDeploymentsForNamespace(String aNamespace,
                                                                         ExtensionsV1beta1Api extensionV1Api)
                                    throws ApiException {

        ExtensionsV1beta1DeploymentList listNamespacedDeployment;

        listNamespacedDeployment =
                extensionV1Api.listNamespacedDeployment(aNamespace,
                                                 null,
                                               null,
                                            null,
                                       null,
                                            null,
                                                  null,
                                         null,
                                         null,
                                                       Boolean.FALSE);

        return listNamespacedDeployment.getItems();
    }


    /**
     * Answer the v1 beta api interface
     * @return ExtensionsV1beta1Api
     */
    private ExtensionsV1beta1Api getExtensionsV1beta1Api() {

        ExtensionsV1beta1Api tempExtensionV1Api;

        this.validateCoreApiObjectExists();
        tempExtensionV1Api = new ExtensionsV1beta1Api();
        tempExtensionV1Api.setApiClient(this.getApiObject().getApiClient());

        return tempExtensionV1Api;
    }

    /**
     * Validate api object exists
     */
    private void validateCoreApiObjectExists() {

        if (this.getApiObject() == null) {

            throw new IllegalStateException("Not initialized");
        }
    }


}
