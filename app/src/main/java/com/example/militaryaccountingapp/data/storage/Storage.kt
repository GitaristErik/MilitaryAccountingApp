package com.example.militaryaccountingapp.data.storage

import com.example.militaryaccountingapp.domain.helper.Results

interface Storage<Key, Data> {

    suspend fun save(key: Key, data: Data): Results<Void?>

    suspend fun load(key: Key): Results<Data>

}