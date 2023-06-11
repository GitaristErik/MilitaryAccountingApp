package com.example.militaryaccountingapp.data.storage

import com.example.militaryaccountingapp.domain.helper.Result

interface Storage<Key, Data> {

    suspend fun save(key: Key, data: Data): Result<Void?>

    suspend fun load(key: Key): Result<Data>

}