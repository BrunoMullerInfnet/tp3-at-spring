do $$
declare
    r record;
begin
    for r in
        select tablename
        from pg_tables
        where schemaname = 'aventura'
    loop
        execute format('drop table if exists aventura.%I cascade', r.tablename);
    end loop;
end $$;