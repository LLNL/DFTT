/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.core.dataAccess.database;

public class ProcedureNames {

    public static final String SCHEMA = "dftt";
    public static final String BEST_STATION_EPOCH_PROC = SCHEMA + ".STATION_UTIL.GET_BEST_STATION_EPOCH";
    public static final String CREATE_ARRIVAL_GROUP_PROC = SCHEMA + ".bulletin_loading.maybe_create_arrival_group";
    public static final String SET_EVENT_PRIME_PROC = SCHEMA + ".ORIGIN_UTIL.SET_EVENT_PRIME";
    public static final String CREATE_UPDATE_ARRIVAL_GROUP_PROC = SCHEMA + ".arrival_util.build_or_update_arrival_group";
    
    public static final String UPDATE_STATION_GROUP_PROC = "{call "+SCHEMA+".adsl_station_util.update_station_group(?)}";

    public static final String GET_GEO_REGION_NAME_FUNC = SCHEMA + ".geo_region.get_geo_region_name";

}
