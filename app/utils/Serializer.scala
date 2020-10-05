package utils

import java.io._

object Serializer {
  implicit class SerializeExtension(t: Serializable) {
    def serialize: Array[Byte] = {
      val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(stream)
      oos.writeObject(t)
      oos.close()
      stream.toByteArray
    }

  }
  implicit class DeserializeExtension(b: Array[Byte]) {
    def deserialize[T <: Serializable]: T = {
      val ois = new ObjectInputStream(new ByteArrayInputStream(b))
      val value = ois.readObject()
      ois.close()
      value.asInstanceOf[T]
    }
  }

//  implicit def ser2arr(t: Serializable): Array[Byte] = t.serialize
//  implicit def arr2ser[T <: Serializable](b: Array[Byte]): T = b.deserialize
}
