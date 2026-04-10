package com.example.guilda_aventureiros_v3.tests;

import com.example.guilda_aventureiros_v3.models.*;
import com.example.guilda_aventureiros_v3.models.Enum.UserStatus;
import com.example.guilda_aventureiros_v3.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve comprovar usuario, organizacao, roles e permissoes")
    void deveComprovarRelacionamentosDeUsuarioRoleEPermissao() {
        Organizacao org = new Organizacao();
        org.setNome("Guilda de Teste");
        org.setAtivo(true);
        org.setCreatedAt(new Date());
        org = entityManager.persistFlushFind(org);

        Permissions perm = new Permissions();
        perm.setCode("MANAGE_QUESTS");
        perm.setDescricao("Pode criar e deletar quests");
        perm = entityManager.persistFlushFind(perm);

        Role role = new Role();
        role.setNome("MESTRE");
        role.setDescricao("Líder da Guilda");
        role.setOrganizacao(org);
        role.setCreatedAt(OffsetDateTime.now());
        role.getPermissions().add(perm);
        role = entityManager.persistFlushFind(role);

        Usuario user = new Usuario();
        user.setNome("Arthur");
        user.setEmail("arthur@excalibur.com");
        user.setSenhaHash("hash_seguro");
        user.setStatus(UserStatus.ATIVO);
        user.setOrganizacao(org);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user = entityManager.persistFlushFind(user);

        UserRoles ur = new UserRoles();
        ur.setUsuario(user);
        ur.setRole(role);
        ur.setGrantedAt(OffsetDateTime.now());

        entityManager.persist(ur);
        entityManager.flush();
        entityManager.clear();

        Usuario usuarioBuscado = usuarioRepository.findById(user.getId()).orElseThrow();
        assertEquals("Arthur", usuarioBuscado.getNome());
        assertEquals("arthur@excalibur.com", usuarioBuscado.getEmail());
        assertEquals(UserStatus.ATIVO, usuarioBuscado.getStatus());

        assertThat(usuarioBuscado.getOrganizacao()).isNotNull();
        assertEquals("Guilda de Teste", usuarioBuscado.getOrganizacao().getNome());

        List<UserRoles> userRoles = entityManager.getEntityManager()
                .createQuery("SELECT ur FROM UserRoles ur WHERE ur.usuario.id = :uid", UserRoles.class)
                .setParameter("uid", usuarioBuscado.getId())
                .getResultList();

        assertThat(userRoles).hasSize(1);
        Role roleDoUsuario = userRoles.get(0).getRole();
        assertEquals("MESTRE", roleDoUsuario.getNome());
        assertEquals("Guilda de Teste", roleDoUsuario.getOrganizacao().getNome());

        assertThat(roleDoUsuario.getPermissions())
                .extracting(Permissions::getCode)
                .contains("MANAGE_QUESTS");
    }
}
