package com.example.militaryaccountingapp.data.repository

/*

class BarcodeRepositoryImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
//    val currentUserUseCase: CurrentUserUseCase
)// : BarcodeRepository
{

    companion object {
        private const val BARCODES = "barcodes"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestoreInstance.collection(BARCODES)


    suspend fun updateCodes(barcodes: List<Barcode>): Results<List<Barcode>> {
        val results = mutableListOf<Barcode>()

        for (barcode in barcodes) {
            try {
                val documentRef = collection.document(barcode.id)
                documentRef.get().await().takeIf { it.exists() }?.let {
                    documentRef.set(barcode, SetOptions.merge()).await()
                    results.add(barcode)
                } ?: run {
                    collection.document()
                        .also { barcode.id = it.id }
                        .set(barcode).await()
                    results.add(barcode)
                }
            } catch (e: Exception) {
                return Results.Failure(e)
            }
        }

        return Results.Success(results)
    }



}*/
