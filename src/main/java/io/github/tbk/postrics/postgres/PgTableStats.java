package io.github.tbk.postrics.postgres;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PgTableStats {
    //String relid; //relid 	oid 	OID of a table
    String schemaname; //schemaname 	name 	Name of the schema that this table is in
    String relname;//relname 	name 	Name of this table
    long seq_scan;//seq_scan 	bigint 	Number of sequential scans initiated on this table
    long seq_tup_read;// 	bigint 	Number of live rows fetched by sequential scans
    long idx_scan;// 	bigint 	Number of index scans initiated on this table
    long idx_tup_fetch;// 	bigint 	Number of live rows fetched by index scans
    long n_tup_ins;// 	bigint 	Number of rows inserted
    long n_tup_upd;// 	bigint 	Number of rows updated (includes HOT updated rows)
    long n_tup_del;// 	bigint 	Number of rows deleted
    long n_tup_hot_upd;// 	bigint 	Number of rows HOT updated (i.e., with no separate index update required)
    long n_live_tup;// 	bigint 	Estimated number of live rows
    long n_dead_tup; //n_dead_tup	bigint 	Estimated number of dead rows
    long n_mod_since_analyze;// 	bigint 	Estimated number of rows modified since this table was last analyzed
    long last_vacuum;// 	timestamp with time zone 	Last time at which this table was manually vacuumed (not counting VACUUM FULL)
    long last_autovacuum;// 	timestamp with time zone 	Last time at which this table was vacuumed by the autovacuum daemon
    long last_analyze;// 	timestamp with time zone 	Last time at which this table was manually analyzed
    long last_autoanalyze;// 	timestamp with time zone 	Last time at which this table was analyzed by the autovacuum daemon
    long vacuum_count;// 	bigint 	Number of times this table has been manually vacuumed (not counting VACUUM FULL)
    long autovacuum_count;// 	bigint 	Number of times this table has been vacuumed by the autovacuum daemon
    long analyze_count;// 	bigint 	Number of times this table has been manually analyzed
    long autoanalyze_count;// 	bigint 	Number of times this table has been analyzed by the autovacuum daemon
}
