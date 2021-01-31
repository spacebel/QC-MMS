FROM python

RUN pip install voila
RUN pip install requests
RUN pip install ipyleaflet
RUN pip install matplotlib
RUN mkdir /EOP_ESA_LP_UC1 
COPY run.sh /run.sh
RUN chmod +x /run.sh

ENTRYPOINT ["/run.sh"]