
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package clonepedia.test.patternextraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.text.preprocessing.filter.StopLabelFilter;
import org.carrot2.text.preprocessing.filter.StopLabelFilterDescriptor;

import com.google.common.collect.Maps;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class ClusteringDocumentList
{
    public static void main(String [] args)
    {
        /* [[[start:clustering-document-list-intro]]]
         * 
         * <div>
         * <p>
         * The easiest way to get started with Carrot2 is to cluster a collection
         * of {@link org.carrot2.core.Document}s. Each document can consist of:
         * </p>
         * 
         * <ul>
         * <li>document content: a query-in-context snippet, document abstract or full text,</li>
         * <li>document title: optional, some clustering algorithms give more weight to document titles,</li>
         * <li>document URL: optional, used by the {@link org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, 
         * ignored by other algorithms.</li>
         * </ul>
         * 
         * <p>
         * To make the example short, the code shown below clusters only 5 documents. Use
         * at least 20 to get reasonable clusters. If you have access to the query that generated
         * the documents being clustered, you should also provide it to Carrot2 to get better clusters.
         * </p>
         * </div>
         * 
         * [[[end:clustering-document-list-intro]]]
         */
        {
            // [[[start:clustering-document-list]]]
            /* A few example documents, normally you would need at least 20 for reasonable clusters. */
            final String [] data = new String []
            {
            		"Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns.",

                
                    "A collection of Data Mining links edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining Resources. Resources. Groups ..."
                ,

                
                    "Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings."
                ,

               
                    "Data mining is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform \"data mining\" by automating the creation ..."
                ,

                
                    "Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works."
                ,
            };

            /* Prepare Carrot2 documents */
            final ArrayList<Document> documents = new ArrayList<Document>();
            for (String row : data)
            {
                documents.add(new Document(row, null, ""));
            }

            /* A controller to manage the processing pipeline. */
            final Controller controller = ControllerFactory.createSimple();

            /*
             * Perform clustering by topic using the Lingo algorithm. Lingo can 
             * take advantage of the original query, so we provide it along with the documents.
             */
            final Map<String, Object> attributes = Maps.newHashMap();
            attributes.put(AttributeNames.DOCUMENTS, documents);
            attributes.put(StopLabelFilterDescriptor.Keys.ENABLED, Boolean.TRUE);
            final ProcessingResult byTopicClusters = controller.process(attributes,
                LingoClusteringAlgorithm.class);
            final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
            
            System.out.println(System.getProperty("java.class.path"));
            /* Perform clustering by domain. In this case query is not useful, hence it is null. */
            /*final ProcessingResult byDomainClusters = controller.process(documents, null,
                ByUrlClusteringAlgorithm.class);*/
            // [[[end:clustering-document-list]]]
            
            ConsoleFormatter.displayClusters(clustersByTopic);
           
       }
    }
}
