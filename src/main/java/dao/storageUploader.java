package dao;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

public class storageUploader {

    private BlobContainerClient containerClient;

    public storageUploader() {
        String accountName = ("khandkercsc311storage");
        String accountKey = System.getenv("storagekey");
        String connectionString = String.format(
                "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                accountName, accountKey
        );

        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName("media-files")
                .buildClient();
    }


    public void uploadFile(String filePath, String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath);
    }
    public BlobContainerClient getContainerClient(){
        return containerClient;
    }
}