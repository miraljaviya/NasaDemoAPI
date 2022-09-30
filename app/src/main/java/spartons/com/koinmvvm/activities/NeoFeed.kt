package spartons.com.koinmvvm.activities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NeoFeed(
    @SerialName(value = "element_count") val elementCount: Int,
    @SerialName(value = "near_earth_objects") val asteroidsByDate: Map<String, List<NearEarthObject>>

)

@Serializable
data class NearEarthObject(
    @SerialName(value = "id") val id: String? = null,
    @SerialName(value = "name") val name: String? = null,
    @SerialName(value = "nasa_jpl_url") val nasaJplUrl: String? = null,
    @SerialName(value = "absolute_magnitude_h") val absoluteMagnitude: Float? = null,
    @SerialName(value = "estimated_diameter") val estimatedDiameter: NeoEstimatedDiameter? = null,
    @SerialName(value = "is_potentially_hazardous_asteroid") val isPotentiallyHazardousAsteroid: Boolean? = null,
    @SerialName(value = "close_approach_data") val closeApproachData: List<NeoCloseApproachData>? = null,
    @SerialName(value = "orbital_data") val orbitalData: NeoOrbitalData? = null
)

@Serializable
data class NeoEstimatedDiameter(
    @SerialName(value =  "kilometers") val kilometers: NeoDiameterRange? = null
)

@Serializable
data class NeoDiameterRange(
    @SerialName(value =  "estimated_diameter_min") val minimumDiameter: Double? = null,
    @SerialName(value =  "estimated_diameter_max") val maximumDiameter: Double? = null
)

@Serializable
data class NeoCloseApproachData(
    @SerialName(value =  "close_approach_date") val approachDate: String? = null,
    @SerialName(value =  "epoch_date_close_approach") val approachEpochDate: Long? = null,
    @SerialName(value =  "relative_velocity") val relativeVelocity: NeoRelativeVelocity? = null,
    @SerialName(value =  "miss_distance") val missDistance: NeoMissDistance? = null
)

@Serializable
data class NeoRelativeVelocity(
    @SerialName(value =  "kilometers_per_hour") val kilometersPerHour: Double? = null
)

@Serializable
data class NeoMissDistance(
    @SerialName(value =  "kilometers") val kilometers: Double? = null,
    @SerialName(value =  "lunar") val lunar: Double? = null
)

@Serializable
data class NeoOrbitalData(
    @SerialName(value =  "orbit_class") val orbitClass: NeoOrbitClass? = null
)

@Serializable
data class NeoOrbitClass(
    @SerialName(value =  "orbit_class_description") val description: String? = null
)