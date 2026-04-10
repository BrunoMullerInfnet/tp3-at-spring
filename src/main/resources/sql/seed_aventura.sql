begin;

do $$
declare
    v_org_id bigint;
    v_org2_id bigint;
    v_user_id bigint;
    v_org2_user_id bigint;
    v_adv3_id bigint;
    v_adv4_id bigint;
    v_adv1_id bigint;
    v_adv2_id bigint;
    v_m1_id bigint;
    v_m2_id bigint;
    v_m3_id bigint;
    v_m4_id bigint;
    v_m5_id bigint;
    v_m6_id bigint;
begin
    truncate table
        aventura.participacoes_missao,
        aventura.companheiros,
        aventura.missoes,
        aventura.aventureiros
    restart identity;

    select id
      into v_org_id
      from audit.organizacoes
     order by id
     limit 1;

    if v_org_id is null then
        raise exception 'Nenhuma organizacao encontrada em audit.organizacoes';
    end if;

    select id
      into v_org2_id
      from audit.organizacoes
     where id = 2;

    if v_org2_id is null then
        raise exception 'Organizacao 2 nao encontrada em audit.organizacoes';
    end if;

    select u.id
      into v_user_id
      from audit.usuarios u
     where u.organizacao_id = v_org_id
     order by u.id
     limit 1;

    if v_user_id is null then
        raise exception 'Nenhum usuario encontrado para a organizacao %', v_org_id;
    end if;

    select u.id
      into v_org2_user_id
      from audit.usuarios u
     where u.organizacao_id = v_org2_id
     order by u.id
     limit 1;

    insert into aventura.aventureiros
        (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
    values
        (v_org_id, v_user_id, 'Arthas', 'GUERREIRO', 20, true, now(), now())
    returning id into v_adv1_id;

    insert into aventura.aventureiros
        (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
    values
        (v_org_id, v_user_id, 'Luna', 'MAGO', 12, true, now(), now())
    returning id into v_adv2_id;

    insert into aventura.aventureiros
        (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
    values
        (v_org_id, v_user_id, 'Kael', 'ARQUEIRO', 14, true, now(), now()),
        (v_org_id, v_user_id, 'Selene', 'CLERIGO', 11, true, now(), now()),
        (v_org_id, v_user_id, 'Ragnar', 'GUERREIRO', 18, true, now(), now());

    if v_org2_user_id is not null then
        insert into aventura.aventureiros
            (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
        values
            (v_org2_id, v_org2_user_id, 'Darian', 'GUERREIRO', 19, true, now(), now())
        returning id into v_adv3_id;

        insert into aventura.aventureiros
            (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
        values
            (v_org2_id, v_org2_user_id, 'Ayla', 'MAGO', 12, true, now(), now())
        returning id into v_adv4_id;

        insert into aventura.aventureiros
            (organizacao_id, usuario_cadastro_id, nome, classe, nivel, ativo, created_at, updated_at)
        values
            (v_org2_id, v_org2_user_id, 'Mira', 'BARDO', 9, true, now(), now()),
            (v_org2_id, v_org2_user_id, 'Thorne', 'LADINO', 16, true, now(), now()),
            (v_org2_id, v_org2_user_id, 'Elora', 'MAGO', 13, true, now(), now());
    else
        raise notice 'Organizacao 2 sem usuario; seed de aventureiros da org 2 nao foi inserido.';
    end if;

    insert into aventura.companheiros
        (aventureiro_id, nome, especie, indice_lealdade)
    values
        (v_adv1_id, 'Fenrir', 'LOBO', 92);

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org_id, 'Ruinas do Norte', 'ALTO', 'EM_ANDAMENTO', now(), now(), null)
    returning id into v_m1_id;

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org_id, 'Bosque Sombrio', 'MEDIO', 'PLANEJADA', now(), null, null)
    returning id into v_m2_id;

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org_id, 'Templo Esquecido', 'LENDARIO', 'CONCLUIDA', now(), now() - interval '2 days', now() - interval '1 day')
    returning id into v_m3_id;

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org2_id, 'Cripta de Gelo', 'ALTO', 'EM_ANDAMENTO', now(), now(), null)
    returning id into v_m4_id;

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org2_id, 'Passagem Rubra', 'MEDIO', 'PLANEJADA', now(), null, null)
    returning id into v_m5_id;

    insert into aventura.missoes
        (organizacao_id, titulo, nivel_perigo, status, created_at, iniciada_em, terminada_em)
    values
        (v_org2_id, 'Arena dos Ecos', 'BAIXO', 'CONCLUIDA', now(), now() - interval '3 days', now() - interval '2 days')
    returning id into v_m6_id;

    insert into aventura.participacoes_missao
        (missao_id, aventureiro_id, papel_missao, recompensa_ouro, destaque, registrada_em)
    values
        (v_m1_id, v_adv1_id, 'LIDER', 100.00, true, now()),
        (v_m1_id, v_adv2_id, 'SUPORTE', 50.00, false, now()),
        (v_m2_id, v_adv2_id, 'EXPLORADOR', 40.00, false, now()),
        (v_m3_id, v_adv1_id, 'VANGUARDA', 120.00, true, now());

    if v_org2_user_id is not null then
        insert into aventura.participacoes_missao
            (missao_id, aventureiro_id, papel_missao, recompensa_ouro, destaque, registrada_em)
        values
            (v_m4_id, v_adv3_id, 'LIDER', 110.00, true, now()),
            (v_m4_id, v_adv4_id, 'SUPORTE', 55.00, false, now()),
            (v_m5_id, v_adv4_id, 'EXPLORADOR', 35.00, false, now()),
            (v_m6_id, v_adv3_id, 'VANGUARDA', 90.00, true, now());
    end if;

    raise notice 'Seed org % -> usuario: %, missoes: [%, %, %], aventureiros principais: %, %',
        v_org_id, v_user_id, v_m1_id, v_m2_id, v_m3_id, v_adv1_id, v_adv2_id;
    raise notice 'Seed org 2 -> usuario: %, aventureiros: %, %',
        v_org2_user_id, v_adv3_id, v_adv4_id;
    raise notice 'Seed org 2 -> missoes: [%, %, %]', v_m4_id, v_m5_id, v_m6_id;
end $$;

commit;
