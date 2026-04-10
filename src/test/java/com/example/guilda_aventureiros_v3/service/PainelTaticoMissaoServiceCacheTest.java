package com.example.guilda_aventureiros_v3.service;

import com.example.guilda_aventureiros_v3.repository.MvPainelTaticoMissaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PainelTaticoMissaoService.class, PainelTaticoMissaoServiceCacheTest.CacheTestConfig.class})
class PainelTaticoMissaoServiceCacheTest {

    @Autowired
    private PainelTaticoMissaoService painelTaticoMissaoService;

    @MockBean
    private MvPainelTaticoMissaoRepository mvPainelTaticoMissaoRepository;

    @Test
    void duasChamadasSeguidasExecutamApenasUmaConsultaAoRepositorio() {
        when(mvPainelTaticoMissaoRepository.findTop10UltimosDias(any()))
                .thenReturn(Collections.emptyList());

        painelTaticoMissaoService.obterTop10UltimosDias();
        painelTaticoMissaoService.obterTop10UltimosDias();

        verify(mvPainelTaticoMissaoRepository, times(1)).findTop10UltimosDias(any());
    }

    @Configuration
    @EnableCaching
    static class CacheTestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("painelTaticoTop10");
        }
    }
}
