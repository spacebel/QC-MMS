# QC-MMS Project

The QC-MMS project goal is to design and implement a Quality Control Metadata Management System for Land Monitoring Services.
The purpose is  to assure traceability of all processing metadata (with known data provenance), allow repeatability of the processed intermediate results as well as the final products, 
early identification of potential risks in the production stages and report standardized QA results.
This project is a GSTP ESA project implemented by [Spacebel](http://www.spacebel.be) and Mapradix. 

# QC-MMS Components
This repository publishes the QC-MMS components implemented by Spacebel and published under Apache License 2.0. The implementation of these components uses third party packages published under licenses listed in the 3rd-party-licenses text file.

## QI_MetadataCatalog
The Quality Indicators (QIs)  Metadata Catalog provides a REST interface to manage (insert, update, delete, retrieve) the metadata information including the quality information (quality indicators) 
associated to the different products and processing steps involved in the land monitoring product generation. The catalogue also stores the Quality Report definition that can be used to generate a Quality report during the processing of the Land Product.
The Catalog provides [an OpenSearch interface](https://docs.opengeospatial.org/is/13-026r8/13-026r8.html) to retrieve the product metadata that are returned following the application/geo+json ([OGC 17-047](https://docs.opengeospatial.org/is/17-047r1/17-047r1.html)) media type format.

## QA_ReportMgr

The QA Report Manager is a Web client for the QI Metadata Catalog. It allows to search and display the product quality metadata stored in the catalog. 
It also allows to display the Quality Report associated to a Land Product.

## QA Report
This component based on the Voilà OSS allows to visualize a live Quality Report as the ouput of a Jupyter notebook execution.




