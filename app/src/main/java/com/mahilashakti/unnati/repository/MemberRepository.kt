package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.database.MemberDao
import kotlinx.coroutines.flow.Flow

class MemberRepository(private val memberDao: MemberDao) {

    fun getAllMembers(): Flow<List<MemberEntity>> = memberDao.getAllMembers()

    fun searchMembers(query: String): Flow<List<MemberEntity>> = memberDao.searchMembers(query)

    suspend fun getMemberById(id: String): MemberEntity? = memberDao.getMemberById(id)

    suspend fun insertMember(member: MemberEntity) = memberDao.insertMember(member)

    suspend fun updateMember(member: MemberEntity) = memberDao.updateMember(member)

    suspend fun deleteMember(member: MemberEntity) = memberDao.deleteMember(member)
}
