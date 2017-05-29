/*
 * Copyright © 2013-2016 BLT, Co., Ltd. All Rights Reserved.
 */

package com.blt.talk.service.internal.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blt.talk.common.constant.DBConstant;
import com.blt.talk.common.util.CommonUtils;
import com.blt.talk.service.internal.SessionService;
import com.blt.talk.service.jpa.entity.IMRecentSession;
import com.blt.talk.service.jpa.repository.IMRecentSessionRepository;
import com.blt.talk.service.jpa.util.JpaRestrictions;
import com.blt.talk.service.jpa.util.SearchCriteria;

/**
 * 
 * @author 袁贵
 * @version 1.0
 * @since  1.0
 */
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private IMRecentSessionRepository recentSessionRepository;
    
    /* (non-Javadoc)
     * @see com.blt.talk.service.internal.SessionService#addSession(long, long, int)
     */
    @Override
    public long addSession(long userId, long peerId, int type) {
        byte status = 0;
        int time = CommonUtils.currentTimeSeconds();

        // 查询已有数据
        SearchCriteria<IMRecentSession> recentSessionCriteria = new SearchCriteria<>();
        recentSessionCriteria.add(JpaRestrictions.eq("userId", userId, false));
        recentSessionCriteria.add(JpaRestrictions.eq("peerId", peerId, false));
        recentSessionCriteria.add(JpaRestrictions.eq("type", type, false));

        List<IMRecentSession> recentSessions =
                recentSessionRepository.findAll(recentSessionCriteria, new Sort(Sort.Direction.DESC, "updated"));

        IMRecentSession recentSession;
        if (!recentSessions.isEmpty()) {
            recentSession = recentSessions.get(0);
            recentSession.setStatus(status);
            recentSession.setUpdated(time);
        } else {

            byte sesstionType = (byte) type;
            recentSession = new IMRecentSession();
            recentSession.setUserId(userId);
            recentSession.setPeerId(peerId);
            recentSession.setType(sesstionType);
            recentSession.setStatus(status);
            recentSession.setCreated(time);
            recentSession.setUpdated(time);
        }

        recentSession = recentSessionRepository.save(recentSession);

        return recentSession.getId();
    }

    /* (non-Javadoc)
     * @see com.blt.talk.service.internal.SessionService#getSessionId(long, long, int, boolean)
     */
    @Override
    public long getSessionId(long userId, long peerId, int type, boolean isAll) {
        byte status = 0;

        // 查询已有数据
        SearchCriteria<IMRecentSession> recentSessionCriteria = new SearchCriteria<>();
        recentSessionCriteria.add(JpaRestrictions.eq("userId", userId, false));
        recentSessionCriteria.add(JpaRestrictions.eq("peerId", peerId, false));
        recentSessionCriteria.add(JpaRestrictions.eq("type", type, false));
        if (!isAll) {
            recentSessionCriteria.add(JpaRestrictions.eq("status", status, false));
        }

        List<IMRecentSession> recentSessions =
                recentSessionRepository.findAll(recentSessionCriteria, new Sort(Sort.Direction.DESC, "updated"));

        if (!recentSessions.isEmpty()) {
            recentSessions.get(0).getId();
        }
        return DBConstant.INVALIAD_VALUE;
    }

    /* (non-Javadoc)
     * @see com.blt.talk.service.internal.SessionService#remove(long)
     */
    @Override
    public void remove(long sessionId) {
        byte status = 1;
        int time = CommonUtils.currentTimeSeconds();
        IMRecentSession session = recentSessionRepository.findOne(sessionId);
        if (session != null) {
            session.setStatus(status);
            session.setUpdated(time);
            recentSessionRepository.save(session);
        }
    }

    /* (non-Javadoc)
     * @see com.blt.talk.service.internal.SessionService#update(long, int)
     */
    @Override
    public void update(long sessionId, int time) {
        IMRecentSession session = recentSessionRepository.findOne(sessionId);
        session.setUpdated(time);

        recentSessionRepository.save(session);
    }

    
}
