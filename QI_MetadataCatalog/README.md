# QI_MetadataCatalog

The Quality Indicators (QIs)  Metadata Catalog provides a REST interface to manage (insert, update, delete, retrieve) the metadata information including the quality information (quality indicators) 
associated to the different products and processing steps involved in the land monitoring products generation. The catalogue also stores the Quality Report definition that can be used to generate a Quality report
during the processing of the Land Product.
The Catalog provides an OpenSearch interface to retrieve the product metadata that are returned following the application/geo+json (OGC 17-047) media type format.
The Catalog implementation is based on Solr.
