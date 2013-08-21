/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.shared.requests.query.responses;

import java.io.Serializable;

import org.n52.shared.requests.query.ResultPage;
import org.n52.shared.requests.query.ResultPager;
import org.n52.shared.requests.query.queries.QueryRequest;

/**
 * A response object containing the results of a {@link QueryRequest}. The results can be either paged (a
 * sub-collection of all available results) or a complete collection of all results available.
 */
public abstract class QueryResponse<T> implements Serializable {

    private static final long serialVersionUID = 8964914430932650368L;

    private String serviceUrl;

    private ResultPage<T> results;

    private boolean paged;

    protected QueryResponse() {
        // for serialization
    }

    public QueryResponse(String serviceUrl) {
        this(serviceUrl, null);
    }

    public QueryResponse(String serviceUrl, T[] results) {
        this.serviceUrl = serviceUrl;
        setResults(results);
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    /**
     * @return the results.
     */
    public T[] getResults() {
        return results.getResults();
    }

    /**
     * Sets the given results as complete collection of all results available.
     * 
     * @param results
     *        the results to set.
     * @see #setResultPage(ResultPage)
     */
    public void setResults(T[] results) {
        int size = results == null ? 0 : results.length;
        this.results = new ResultPager().createPageFrom(results, 0, size);
        this.paged = false;
    }

    /**
     * Sets the given page as a sub-collection of all results available.
     * 
     * @param resultPage
     *        the result page to set.
     * @see #setResults(T[])
     */
    public void setResultPage(ResultPage<T> resultPage) {
        this.results = resultPage;
        this.paged = true;
    }

    /**
     * Returns all query results on a page, no matter if it is a sub-collection or all results available.
     * 
     * @return the results on a page.
     */
    public ResultPage<T> getPagedResults() {
        return results;
    }

    /**
     * @return <code>true</code> the query results are paged, <code>false</code> otherwise.
     */
    public boolean isPaged() {
        return paged;
    }

}
