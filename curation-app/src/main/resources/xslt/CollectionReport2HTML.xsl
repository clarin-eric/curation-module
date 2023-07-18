<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com">
    <xsl:function name="functx:capitalize-first" as="xs:string?"
                  xmlns:functx="http://www.functx.com">
        <xsl:param name="arg" as="xs:string?"/>

        <xsl:sequence select="
   concat(upper-case(substring($arg,1,1)),
             substring($arg,2))
 "/>

    </xsl:function>
	<xsl:template match="/collectionReport">
		<html>
			<head>
			</head>
			<body>
				<div class="creationTime">
					created at
					<xsl:value-of select="./@creationTime" />
				</div>
				<div class="download">
					download as
					<a>
						<xsl:attribute name="href">
					    <xsl:text>/download/collection/</xsl:text>
					    <xsl:value-of select="//fileReport/provider" />
				    </xsl:attribute>
						<xsl:text>xml</xsl:text>
					</a>
					<xsl:text> </xsl:text>
					<a>
						<xsl:attribute name="href">
                   <xsl:text>/download/collection/</xsl:text>
                   <xsl:value-of select="//fileReport/provider" />
                   <xsl:text>?format=json</xsl:text>
                </xsl:attribute>
						<xsl:text>json</xsl:text>
					</a>
				</div>
				<div class="clear" />
				<h1>Collection Report</h1>
				<h3>
					Collection name:
					<xsl:value-of
						select="replace(//fileReport/provider,'_',' ')" />
				</h3>
				
				     <!-- scoreTable -->

			   <table class="reportTable">
			     <thead>
			        <tr>
			           <th>Section</th>
			           <th>Score</th>
			           <th>Score Percentage</th>
			        </tr>
			     </thead>
			     <tfoot>
                  <tr>
	                  <td>total</td>
	                  <td align="right"><xsl:value-of select="format-number(@aggregatedScore,'###,##0.0')" />
	                  / <xsl:value-of select="format-number(@aggregatedMaxScore,'###,##0.0')" /></td>
	                  <td align="right"><xsl:value-of select="format-number(@scorePercentage, '0.0%')" /></td>
                  </tr>
              </tfoot>
			     <tbody>
			         <xsl:for-each select="*">
			            <xsl:if test="@aggregatedScore">
			               <tr>
			                  <td>
			                  <a>
			                  <xsl:attribute name="href">
			                  <xsl:text>#</xsl:text>
			                  <xsl:value-of select="name(.)" />
			                  </xsl:attribute>
			                  <xsl:choose>
			                     <xsl:when test="name(.) = 'fileReport'">Files</xsl:when>
			                     <xsl:when test="name(.) = 'profileReport'">Profile usage</xsl:when>
			                     <xsl:when test="name(.) = 'headerReport'">Header</xsl:when>
			                     <xsl:when test="name(.) = 'resProxyReport'">Resource proxy</xsl:when>
			                     <xsl:when test="name(.) = 'xmlPopulationReport'">XML population</xsl:when>
			                     <xsl:when test="name(.) = 'xmlValidityReport'">XML validation</xsl:when>
			                     <xsl:when test="name(.) = 'linkcheckerReport'">Link validation</xsl:when>
			                     <xsl:when test="name(.) = 'facetReport'">Facets</xsl:when>
			                     <xsl:otherwise>xxxxxxxxx</xsl:otherwise>
			                  </xsl:choose>
			                  </a>
			                  </td>			                  
			                  <td align="right"><xsl:value-of select="format-number(@aggregatedScore,'###,##0.0')" />
			                  / <xsl:value-of select="format-number(@aggregatedMaxScore,'###,##0.0')" /></td>
			                  <td align="right"><xsl:value-of select="format-number(@scorePercentage,'0.0%')" /></td>
			               </tr>			            
			            </xsl:if>
			         </xsl:for-each>
			     </tbody>    
			   </table> 
			   <br />
			   The above table is based on <xsl:value-of select="format-number(//fileReport/numOfFilesProcessable, '###,##0')" /> processable files.
			   <xsl:if test="//fileReport/numOfFilesNonProcessable>0">
            There are also <xsl:value-of select="format-number(//fileReport/numOfFilesNonProcessable, '###,##0')" /> files in the collection that could not be processed. 
            See <a href="#recordDetails">record details</a> table for more information.
            </xsl:if>
            <br>All per file averages (except in the Files section) are based on the number of processable files</br>
			   <br />
            <br />
            <xsl:apply-templates select="fileReport" />
				<hr />
            <xsl:apply-templates select="headerReport" />
            <hr />
            <xsl:apply-templates select="profileReport" />
            <hr />
            <xsl:apply-templates select="facetReport" />
            <hr />
            <xsl:apply-templates select="resProxyReport" />
            <hr />
            <xsl:apply-templates select="xmlValidityReport" />
            <hr />
            <xsl:apply-templates select="xmlPopulationReport" />
            <hr />
            <xsl:apply-templates select="linkcheckerReport" />
				<hr />
				
           <xsl:if test="./recordDetails/record">
            <hr />
            <details>
               <summary>
                  <h2 id="recordDetails">Record details:</h2>
                  </summary>
                  <p>The record details section shows the particalarities of each record as far as they're of importance for the data provider.</p>
               </details>                  
               <table class="reportTable">
                  <thead>
                     <tr>
                        <th>File</th>
                        <th>Info</th>
                        <th>Validate</th>
                     </tr>
                  </thead>
                  <tbody>
                     <xsl:for-each
                        select="./recordDetails/record">

                        <xsl:if test="not(position() > 100)">
                           <tr>
                              <td>
                                 <a>
                                    <xsl:attribute name="href">/record/<xsl:value-of
                                       select="./@origin" /></xsl:attribute>
                                    <xsl:value-of select="./@origin" />
                                 </a>

                              </td>
                              <td>
                                 <button type="button" class="showUrlInfo btn btn-info"
                                    onClick="toggleInfo(this)">Show</button>
                              </td>
                              <td>
                                 <button type="button" class="btn btn-info">
                                    <xsl:attribute name="onClick">window.open('/curate?url-input=<xsl:value-of
                                       select="./@origin"></xsl:value-of>')</xsl:attribute>
                                    Validate file
                                 </button>
                              </td>
                           </tr>
                           <tr hidden="true">
                              <td colspan="3">
                                 <ul>
                                    <xsl:for-each select="detail">
                                       <li>
                                          Severity: <xsl:value-of select="./severity"></xsl:value-of>, 
                                          Segment: <xsl:value-of select="./segment"></xsl:value-of>, 
                                          Message:  <xsl:value-of select="./message"></xsl:value-of>
                                       </li>
                                    </xsl:for-each>
                                 </ul>
                              </td>
                           </tr>
                        </xsl:if>
                     </xsl:for-each>
                     <xsl:if
                        test="count(./recordDetails/record) > 100">
                        <tr>
                           <td colspan="3">[...] complete list in downloadable report</td>
                        </tr>
                     </xsl:if>
                  </tbody>
               </table>
            </xsl:if>
			</body>
		</html>
	</xsl:template>
	

	
	<!-- fileReport -->
   <xsl:template match="fileReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Files
                  </h2>
               </summary>
               <p>General information on the number of files and the file size.</p>
            </details>
            
            <p>
               Number of files:
               <xsl:value-of select="format-number(./numOfFiles, '###,##0')" />
            </p>
            <p>
               Number of processable files:
               <xsl:value-of select="format-number(./numOfFilesProcessable, '###,##0')" />
            </p>
            <p>
               Total size:
               <xsl:value-of select="format-number(./size, '###,##0')" />
               B
            </p>
            <p>
               Average size:
               <xsl:value-of select="format-number(./avgFileSize, '###,##0')" />
               B
            </p>
            <p>
               Minimal file size:
               <xsl:value-of select="format-number(./minFileSize, '###,##0')" />
               B
            </p>
            <p>
               Maximal file size:
               <xsl:value-of select="format-number(./maxFileSize, '###,##0')" />
               B
            </p>
   </xsl:template>
    
   <!-- headerReport -->
   <xsl:template match="headerReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Header
                  </h2>
               </summary>
               <p>
                  The header section shows information on the availibilty of attribute schemaLocation as well as the elements 
                  MdSelfLink, MdProfile and MdCollectionDisplayName.
                  </p>
            </details>
            <p>
            Number of files with schemaLocation:<xsl:value-of select="format-number(numWithSchemaLocation, '###,##0')" />
            </p>
            <p>
            Number of files where schemaLocation is CR resident: <xsl:value-of select="format-number(numSchemaCRResident, '###,##0')" />
            </p>
            <p>
            Number of files with MdProfile: <xsl:value-of select="format-number(numWithMdProfile, '###,##0')" />
            </p>
            <p>
            Number of files with MdSelfLink: <xsl:value-of select="format-number(numWithMdSelflink, '###,##0')" />
            </p>
            <p>
            Number of files with MdCollectionDisplayName: <xsl:value-of select="format-number(numWithMdCollectionDisplayName, '###,##0')" />
            </p>
   </xsl:template>
     
   <!-- profileReport -->
   <xsl:template match="profileReport">
               <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Profile usage
                  </h2>
               </summary>
               <p>
                  The profile usage section shows information shows which profiles are used how oftenly in a collection. 
                  collection.
               </p>   
            </details>
            <table class="reportTable">
               <thead>
                  <tr>
                     <th>ID</th>
                     <th>Is public</th>
                     <th>Score</th>
                     <th>Count</th>
                  </tr>
               </thead>
               <tfoot>
                  <tr>
                     <td colspan="4">
                        Total number of profiles:
                        <xsl:value-of
                           select="./totNumOfProfiles" />
                     </td>
                  </tr>
               </tfoot>
               <tbody>
                  <xsl:for-each
                     select="./profiles/profile">
                     <xsl:sort select="./@score" data-type="number"
                        order="descending" />
                     <xsl:sort select="./@count" data-type="number"
                        order="descending" />
                     <xsl:variable name="profileID">
                        <xsl:value-of select="./@profileId" />
                     </xsl:variable>
                     <tr>
                        <td>
                           <a>
                              <xsl:attribute name="href">
                     <xsl:text>/profile/</xsl:text>
                        <xsl:value-of
                                 select="translate(./@profileId,'.:','__')" />
                        <xsl:text>.html</xsl:text>
                       </xsl:attribute>
                              <xsl:value-of select="./@profileId" />
                           </a>
                        </td>
                        <td>
                        <xsl:value-of select="./@isPublic" />
                        </td>
                        <td class='text-right'>
                           <xsl:value-of
                              select="format-number(./@score,'0.00')" />
                        </td>
                        <td class='text-right'>
                           <xsl:value-of select="format-number(./@count, '###,##0')" />
                        </td>
                     </tr>
                  </xsl:for-each>
               </tbody>
            </table>      
   </xsl:template>

   
   <!-- facetReport -->
   <xsl:template match="facetReport" >
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Facets
                  </h2>
               </summary>
               <p>The facet section shows the facet coverage within the
                  collection. A facet can be covered by the instance 
                  even when it is not covered by the profile when cross facet mapping is used.
               </p>
            </details>
            
            <table class="reportTable">
               <thead>
                  <tr>
                     <th scope="col">name</th>
                     <th scope="col">coverage</th>
                  </tr>
               </thead>
               <tfoot>
                  <tr>
                     <td colspan="2">
                        <b>
                           average facet-coverage:
                           <xsl:value-of
                              select="format-number(./@avgScore,'0.0%')" />
                        </b>
                     </td>
                  </tr>
               </tfoot>
               <tbody>
                  <xsl:for-each select="./facets/facet">
                     <tr>
                        <td>
                           <xsl:value-of select="./@name" />
                        </td>
                        <td class="text-right">
                           <xsl:value-of
                              select="format-number(./@avgCoverage,'0.0%')" />
                        </td>
                     </tr>
                  </xsl:for-each>
               </tbody>
            </table>   
   </xsl:template>
   
   <!-- resProxyReport -->
   <xsl:template match="resProxyReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Resource proxy
                  </h2>
               </summary>
               <p>The resource proxy section shows information on the number of
                  resource proxies on the kind (the mime type) of resources.
                  A resource proxy is a link to an external resource, described by
                  the CMD file.
               </p>
            </details>
            
            <p>
               Total number of resource proxies:
               <xsl:value-of
                  select="format-number(./totNumOfResProxies, '###,##0')" />
            </p>
            <p>
               Average number of resource proxies:
               <xsl:value-of
                  select="format-number(./avgNumOfResProxies,'###,##0.00')" />
            </p>
            <p>
               Total number of resource proxies with MIME:
               <xsl:value-of
                  select="format-number(./totNumOfResProxiesWithMime, '###,##0')" />
            </p>
            <p>
               Average number of resource proxies with MIME:
               <xsl:value-of
                  select="format-number(./avgNumOfResProxiesWithMime,'###,##0.00')" />
            </p>
            <p>
               Total number of resource proxies with reference:
               <xsl:value-of
                  select="format-number(./totNumOfResProxiesWithReference, '###,##0')" />
            </p>
            <p>
               Average number of resource proxies with references:
               <xsl:value-of
                  select="format-number(./avgNumOfResProxiesWithReference,'###,##0.00')" />
            </p>   
   </xsl:template>
   
   <!-- xmlValidityReport -->
   <xsl:template match="xmlValidityReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  XML validation
                  </h2>
               </summary>
               <p>The XML validation section shows the result of a simple
                  validation of each CMD file against its profile. </p>
            </details>
            
            <p>
               Number of XML valid Records:
               <xsl:value-of
                  select="format-number(./totNumOfValidRecords, '###,##0')" />
            </p>
            <p>
               Ratio XML valid Records:
               <xsl:value-of
                  select="format-number(./@avgScore,'0.0%')" />
            </p>   
   </xsl:template>
   
   <!-- xmlPopulationReport -->
   <xsl:template match="xmlPopulationReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  XML population
                  </h2>
               </summary>
               <p>The XML population section shows information on the number of xml
                  elements and the fact if these elements are conatining data. </p>
            </details>
            
            <p>
               Total number of XML elements:
               <xsl:value-of
                  select="format-number(./totNumOfXMLElements, '###,##0')" />
            </p>
            <p>
               Average number of XML elements:
               <xsl:value-of
                  select="format-number(./avgNumOfXMLElements,'###,##0.00')" />
            </p>
            <p>
               Total number of simple XML elements:
               <xsl:value-of
                  select="format-number(./totNumOfXMLSimpleElements, '###,##0')" />
            </p>
            <p>
               Average number of simple XML elements:
               <xsl:value-of
                  select="format-number(./avgNumOfXMLSimpleElements,'###,##0.00')" />
            </p>
            <p>
               Total number of empty XML elements:
               <xsl:value-of
                  select="format-number(./totNumOfXMLEmptyElements, '###,##0')" />
            </p>
            <p>
               Average number of empty XML elements:
               <xsl:value-of
                  select="format-number(./avgXMLEmptyElements,'###,##0.00')" />
            </p>
            <p>
               Average rate of populated elements:
               <xsl:value-of
                  select="format-number(./avgRateOfPopulatedElements,'###,##0.0%')" />
            </p>   
   </xsl:template>
   
   <!-- linkcheckerReport -->
   <xsl:template match="linkcheckerReport">
            <details>
               <summary>
                  <h2>
                  <xsl:attribute name="id">
                  <xsl:value-of select="name(.)" />
                  </xsl:attribute>
                  Link validation
                  </h2>
               </summary>
               <p>The link validation section shows information on the number of
                  links and the results of link checking for the links which
                  have been checked so far.
               </p>
            </details>
            
            <p>
               Total number of links:
               <xsl:value-of
                  select="format-number(./totNumOfLinks, '###,##0')" />
            </p>
            <p>
               Average number of links:
               <xsl:value-of
                  select="format-number(./avgNumOfLinks,'###,##0.00')" />
            </p>
            <p>
               Total number of unique links:
               <xsl:value-of
                  select="format-number(./totNumOfUniqueLinks, '###,##0')" />
            </p>
           <p>
               Average number of unique links:
               <xsl:value-of
                  select="format-number(./avgNumOfUniqueLinks,'###,##0.00')" />
            </p>
            <p>
               Total number of checked links:
               <xsl:value-of
                  select="format-number(./totNumOfCheckedLinks, '###,##0')" />
            </p>
            <p>
               Ratio of valid links:
               <xsl:value-of
                  select="format-number(./ratioOfValidLinks,'0.0%')" />
            </p>

            <xsl:if test="./linkchecker/statistics">
               <h3>Link Checking Results</h3>
   
               <table class="reportTable">
                  <thead>
                     <tr>
                        <th scope="col">Category</th>
                        <th scope="col">Count</th>
                        <th scope="col">Average Response Duration(ms)</th>
                        <th scope="col">Max Response Duration(ms)</th>
                     </tr>
                  </thead>
                  <tbody>
                     <xsl:for-each
                        select="./linkchecker/statistics">
   
                        <tr>
                        <xsl:attribute name="class">
                        <xsl:value-of select="./@category" />
                        </xsl:attribute>
                           <td align="right">
                              <a>
                              <xsl:attribute name="href">
                              <xsl:text>/linkchecker/</xsl:text>
                              <xsl:value-of select="//fileReport/provider" />
                              <xsl:text>#</xsl:text>
                              <xsl:value-of select="./@category" />
                              </xsl:attribute>
                              <xsl:value-of select="./@category" />
                              </a>
                           </td>
   
                           <td align="right">
                              <xsl:value-of select="format-number(./@count, '###,##0')" />
                           </td>
                           <td align="right">
                              <xsl:value-of
                                 select="format-number(./@avgRespTime, '###,##0.0')" />
                           </td>
                           <td align="right">
                              <xsl:value-of
                                 select="format-number(./@maxRespTime, '###,##0.0')" />
                           </td>
                        </tr>   
                     </xsl:for-each>
                  </tbody>
               </table>
           </xsl:if>   
   </xsl:template>
</xsl:stylesheet>