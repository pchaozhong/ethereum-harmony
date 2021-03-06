/*
 * Copyright 2015, 2016 Ether.Camp Inc. (US)
 * This file is part of Ethereum Harmony.
 *
 * Ethereum Harmony is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ethereum Harmony is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ethereum Harmony.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ethercamp.harmony.service;

import lombok.extern.slf4j.Slf4j;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.Repository;
import org.ethereum.facade.Ethereum;
import org.ethereum.mine.EthashListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Stan Reshetnyk on 19.08.16.
 */
@Slf4j(topic = "harmony")
@Service
public class PrivateMinerService {

    @Autowired
    Environment env;

    @Autowired
    Ethereum ethereum;

    @Autowired
    SystemProperties config;

    @Autowired
    public Repository repository;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        final boolean isPrivateNetwork = env.getProperty("networkProfile", "").equalsIgnoreCase("private");
        log.info("isPrivateNetwork: " + isPrivateNetwork);
        ethereum.getBlockMiner().addListener(new EthashListener() {
            @Override
            public void miningStarted() {
                log.info("miningStarted");
            }

            @Override
            public void miningStopped() {
                log.info("miningStopped");
            }

            @Override
            public void blockMiningStarted(Block block) {
                log.info("new block mining started");
            }

            @Override
            public void blockMined(Block block) {
                log.info("blockMined");
            }

            @Override
            public void blockMiningCanceled(Block block) {
                log.info("blockMiningCanceled");
            }

            @Override
            public void onDatasetUpdate(DatasetStatus datasetStatus) {
                log.info("Dataset status updated: {}", datasetStatus);
            }
        });
        if (config.minerStart()) {
            ethereum.getBlockMiner().startMining();
        }
    }
}
