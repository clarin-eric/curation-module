<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/profile-report">
		<xsl:variable name="schemaLoc">
			<xsl:value-of select="./header-section/schemaLocation" />
		</xsl:variable>
		<html>
			<head>
			</head>
			<body>
				<div class="creation-time">
					created at
					<xsl:value-of select="./@creation-time" />
				</div>
				<div class="download">
					download as
					<a>
						<xsl:attribute name="href">
                   <xsl:text>/download/xml/profiles/</xsl:text>
                   <xsl:value-of
							select="translate(./header-section/id,'.:','__')" />
                </xsl:attribute>
						<xsl:text>xml</xsl:text>
					</a>
					<xsl:text> </xsl:text>
					<a>
						<xsl:attribute name="href">
                   <xsl:text>/download/json/profiles/</xsl:text>
                   <xsl:value-of
							select="translate(./header-section/id,'.:','__')" />
                </xsl:attribute>
						<xsl:text>json</xsl:text>
					</a>
				</div>
				<div class="clear" />
				<h1>CMD Profile Report</h1>
				<table class="reportTable">
					<tr>
						<th>Name</th>
						<td>
							<xsl:value-of select="./header-section/name" />
						</td>
					</tr>
					<tr>
						<th>ID</th>
						<td>
							<xsl:value-of select="./header-section/id" />
						</td>
					</tr>
					<tr>
						<th>Description</th>
						<td>
							<xsl:value-of select="./header-section/description" />
						</td>
					</tr>
					<tr>
						<th>Schema Location</th>

						<td>
							<xsl:choose>
								<xsl:when
									test="starts-with($schemaLoc, 'http://') or starts-with($schemaLoc, 'https://')">
									<a href="{$schemaLoc}" target="_blank">
										<xsl:copy-of select="$schemaLoc" />
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select='$schemaLoc' />
								</xsl:otherwise>
							</xsl:choose>
						</td>

					</tr>
					<tr>
						<th>CMDI Version</th>
						<td>
							<xsl:value-of select="./header-section/cmdiVersion" />
						</td>
					</tr>
					<xsl:if test="./header-section/status">
						<tr>
							<th>Status</th>
							<td>
								<xsl:value-of select="./header-section/status" />
							</td>
						</tr>
					</xsl:if>
				</table>
				<hr />
				<details>
					<summary>
						<h2>Score Section</h2>
					</summary>
					<p>
						The scoring is based on public state of the profile, the
						percentage of elements (except header and resources) with concept
						and
						percentage of defined facets covered by the profile.
						<br />
						For details on scoring, have a look at the
						<a href="/faq">FAQ</a>
						, please.
					</p>
				</details>
				<table class="reportTable">
					<thead>
						<tr>
							<th scope="col">Segment</th>
							<th scope="col">Score</th>
							<th scope="col">Max</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="3">
								<b>
									Total:
									<xsl:value-of
										select="format-number(./@score,'0.00')" />
									Max:
									<xsl:value-of
										select="format-number(./@max-score,'0.00')" />
								</b>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<xsl:for-each select="./score-section/score">
							<tr>
								<td>
									<xsl:value-of select="./@segment" />
								</td>
								<td class="text-right">
									<xsl:value-of
										select="format-number(./@score,'0.00')" />
								</td>
								<td class="text-right">
									<xsl:value-of
										select="format-number(./@maxScore,'0.00')" />
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>

				<hr />

				<details>
					<summary>
						<h2>Facets Section</h2>
					</summary>
					<p>The facet section shows if a specific facet is covered by the
						profile.
						In other words, if the profile defines an element for the facet.
					</p>
				</details>
				<table class="reportTable">
					<thead>
						<tr>
							<th scope="col">Name</th>
							<th scope="col">Covered</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="2">
								<b>
									Covered:
									<xsl:value-of
										select="count(./facets-section/coverage/facet[@coveredByProfile = 'true'])" />
									/
									<xsl:value-of
										select="./facets-section/@numOfFacets" />
									Coverage:
									<xsl:value-of
										select="format-number(./facets-section/@profileCoverage,'0.0%')" />
								</b>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<xsl:for-each
							select="./facets-section/coverage/facet">
							<xsl:sort select="./@coveredByProfile"
								order="descending" />
							<tr>
								<xsl:choose>
									<xsl:when test="./@coveredByProfile = 'false'">
										<td>
											<font color="#d33d3d">
												<xsl:value-of select="./@name" />
											</font>
										</td>
										<td>
											<font color="#d33d3d">
												<xsl:value-of select="./@coveredByProfile" />
											</font>
										</td>
									</xsl:when>
									<xsl:otherwise>
										<td>
											<xsl:value-of select="./@name" />
										</td>
										<td>
											<xsl:value-of select="./@coveredByProfile" />
										</td>
									</xsl:otherwise>
								</xsl:choose>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
				<hr />

				<details>
					<summary>
						<h2>Usage Section</h2>
					</summary>
					<p>The usage section shows in which collection the profile is used</p>
				</details>
				<table class="reportTable">
					<thead>
						<tr>
							<th scope="col">Collection</th>
							<th scope="col">Usage</th>
						</tr>
					</thead>
					<tbody>
						<xsl:if test="count(./usage-section/collection)=0">
							<tr>
								<td colspan="2">profile not used</td>
							</tr>
						</xsl:if>
						<xsl:for-each select="./usage-section/collection">
							<tr>
								<td>
									<xsl:value-of select="./@collectionName" />
								</td>
								<td>
									<xsl:value-of select="./@count" />
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
				<hr />
				<details>
					<summary>
						<h2>Cmd Component Section</h2>
					</summary>
					<p>
						The components section shows information on the kind, id and the
						usage of concepts in the profile.
						<br />
						For more information on componets, have a look at the
						<a
							href="https://www.clarin.eu/content/component-registry-documentation">Component Registry Documentation</a>
						, please.
					</p>
				</details>
				<table class="reportTable">
					<thead>
						<tr>
							<th scope="col">Name</th>
							<th scope="col">Id</th>
							<th scope="col">Count</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="3">
								<b>
									Total:
									<xsl:value-of
										select="./cmd-components-section/@total" />
									Unique:
									<xsl:value-of
										select="./cmd-components-section/@unique" />
									Required:
									<xsl:value-of
										select="./cmd-components-section/@required" />
								</b>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<xsl:for-each
							select="./cmd-components-section/component">
							<tr>
								<xsl:variable name="href">
									http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/components/
									<xsl:value-of select="./@id" />
								</xsl:variable>
								<td>
									<xsl:value-of select="./@name" />
								</td>
								<td>
									<a href="{$href}" target="_blank">
										<xsl:value-of select="./@id" />
									</a>
								</td>
								<td class="text-right">
									<xsl:value-of select="./@count" />
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
				<hr />
				<details>
					<summary>
						<h2>Cmd Concepts Section</h2>
					</summary>
					<p>
						The concepts section shows information on the kind, state and the
						number of concepts used in the profile.
						<br />
						For more information on concepts, have a look at the
						<a href="https://www.clarin.eu/content/clarin-concept-registry">CLARIN Concept Registry</a>
						, please.
					</p>
				</details>
				<p>
					Total number of elements:
					<xsl:value-of select="./cmd-concepts-section/@total" />
				</p>
				<p>
					Number of required elements:
					<xsl:value-of
						select="./cmd-concepts-section/@required" />
				</p>
				<p>
					Number of elements with specified concept:
					<xsl:value-of
						select="./cmd-concepts-section/@withConcept" />
				</p>
				<p>
					Percentage of elements with specified concept:
					<xsl:value-of
						select="format-number(./cmd-concepts-section/@percWithConcept,'0.0%')" />
				</p>
				<table class="reportTable">
					<thead>
						<tr>
							<th scope="col">Concept</th>
							<th scope="col">Status</th>
							<th scope="col">Count</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="3">
								<b>
									Total:
									<xsl:value-of
										select="./cmd-concepts-section/concepts/@total" />
									Unique:
									<xsl:value-of
										select="./cmd-concepts-section/concepts/@unique" />
									Required:
									<xsl:value-of
										select="./cmd-concepts-section/concepts/@required" />
								</b>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<xsl:for-each
							select="./cmd-concepts-section/concepts/concept">
							<tr>
								<xsl:variable name="href">
									<xsl:value-of select="./@uri" />
								</xsl:variable>
								<td>
									<a href="{$href}" title="{$href}" target="_blank">
										<xsl:value-of select="./@prefLabel" />
									</a>
								</td>
								<td>
									<xsl:value-of select="./@status" />
								</td>
								<td class="text-right">
									<xsl:value-of select="./@count" />
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>


				<xsl:if test="./score-section//issue">

					<hr />
					<details>
						<summary>
							<h2>Issues</h2>
						</summary>
						<p>The issues section shows the number and the kind of issues,
							which might have an impact on the processing of the profile.</p>
					</details>
					<table class="reportTable">
						<thead>
							<tr>
								<th scope="col">Segment</th>
								<th scope="col">Severity</th>
								<th scope="col">Message</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="./score-section/score">
								<xsl:variable name="seg">
									<xsl:value-of select="./@segment" />
								</xsl:variable>
								<xsl:for-each select="./issue">
									<xsl:choose>
										<xsl:when test="@lvl = 'ERROR'">
											<tr>
												<td>
													<font color="#d33d3d">
														<xsl:copy-of select="$seg" />
													</font>
												</td>
												<td>
													<font color="#d33d3d">
														<xsl:value-of select="./@lvl" />
													</font>
												</td>
												<td>
													<font color="#d33d3d">
														<xsl:value-of select="./@message" />
													</font>
												</td>
											</tr>
										</xsl:when>
										<xsl:when test="@lvl = 'WARNING'">
											<tr>
												<td>
													<font color="#dbd839">
														<xsl:copy-of select="$seg" />
													</font>
												</td>
												<td>
													<font color="#dbd839">
														<xsl:value-of select="./@lvl" />
													</font>
												</td>
												<td>
													<font color="#dbd839">
														<xsl:value-of select="./@message" />
													</font>
												</td>
											</tr>
										</xsl:when>
										<xsl:otherwise>
											<tr>
												<td>
													<xsl:copy-of select="$seg" />
												</td>
												<td>
													<xsl:value-of select="./@lvl" />
												</td>
												<td>
													<xsl:value-of select="./@message" />
												</td>
											</tr>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:for-each>
							</xsl:for-each>
						</tbody>
					</table>
				</xsl:if>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet> 