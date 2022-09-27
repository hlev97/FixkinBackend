package hu.bme.aut.it9p0z.fixkin.user.repository

import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserMongoRepository : MongoRepository<User,String>
