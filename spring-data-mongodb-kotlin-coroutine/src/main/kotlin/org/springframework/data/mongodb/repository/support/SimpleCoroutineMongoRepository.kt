/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.mongodb.repository.support

import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import kotlinx.coroutines.experimental.reactive.awaitSingle
import org.springframework.data.mongodb.core.CoroutineMongoOperations
import org.springframework.data.mongodb.core.CoroutineMongoTemplate
import org.springframework.data.mongodb.repository.CoroutineMongoRepository
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import java.io.Serializable

open class SimpleCoroutineMongoRepository<T, ID: Serializable>(
    entityInformation: MongoEntityInformation<T, ID>,
    mongoOperations: CoroutineMongoOperations
): CoroutineMongoRepository<T, ID> {
    private val reactiveRepo = SimpleReactiveMongoRepository<T, ID>(entityInformation, (mongoOperations as CoroutineMongoTemplate).reactiveMongoOperations)

    suspend override fun <S : T> save(entity: S): S =
        reactiveRepo.save(entity).awaitSingle()

    suspend override fun <S : T> saveAll(entities: Iterable<S>): List<S> =
        reactiveRepo.saveAll(entities).collectList().awaitSingle()

    suspend override fun findById(id: ID): T? =
        reactiveRepo.findById(id).awaitFirstOrDefault(null)

    suspend override fun existsById(id: ID): Boolean =
        reactiveRepo.existsById(id).awaitSingle()

    suspend override fun findAll(): List<T> =
        reactiveRepo.findAll().collectList().awaitSingle()

    suspend override fun findAllById(ids: Iterable<ID>): List<T> =
        reactiveRepo.findAllById(ids).collectList().awaitSingle()

    suspend override fun count(): Long =
        reactiveRepo.count().awaitSingle()

    suspend override fun deleteById(id: ID) {
        reactiveRepo.deleteById(id).awaitFirstOrDefault(null)
    }

    suspend override fun delete(entity: T) {
        reactiveRepo.delete(entity).awaitFirstOrDefault(null)
    }

    suspend override fun deleteAll(entities: Iterable<T>) {
        reactiveRepo.deleteAll().awaitFirstOrDefault(null)
    }

    suspend override fun <S : T> insert(entity: S): S? =
        reactiveRepo.insert(entity).awaitFirstOrDefault(null)

    suspend override fun deleteAll() {
        reactiveRepo.deleteAll().awaitFirstOrDefault(null)
    }
}
