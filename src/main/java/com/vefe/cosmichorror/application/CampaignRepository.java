package com.vefe.cosmichorror.application;

import com.vefe.cosmichorror.domain.Campaign;
import java.util.*;

public interface CampaignRepository { Optional<Campaign> find(UUID id); void save(Campaign campaign); }
