--http://azry.com/blog/?p=44

ALTER DATABASE database-name SET ALLOW_SNAPSHOT_ISOLATION ON
ALTER DATABASE database-name SET SINGLE_USER WITH ROLLBACK IMMEDIATE
ALTER DATABASE database-name SET READ_COMMITTED_SNAPSHOT ON
ALTER DATABASE database-name SET MULTI_USER

--Check
 select name
        , s.snapshot_isolation_state
        , snapshot_isolation_state_desc
        , is_read_committed_snapshot_on
        , recovery_model
        , recovery_model_desc
        , collation_name
    from sys.databases s;

--Hibernate properties
<property name="hibernate.id.new_generator_mappings" value="true"/>
<property name="hibernate.connection.isolation" value="4096"/>

--Datasource properties
<new-connection-sql>SET TRANSACTION ISOLATION LEVEL SNAPSHOT;</new-connection-sql>
<transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>

