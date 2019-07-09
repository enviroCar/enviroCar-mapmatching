package org.envirocar.processing.mapmatching.serde

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.envirocar.processing.mapmatching.models.json.MeasurementJson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * @author dewall
 */
class MeasurementDeserializerTest {

    lateinit var  objectMapper: ObjectMapper

    @Before
    fun init(){
        objectMapper = ObjectMapper()
                .registerModule(EnviroCarJSONModule())
    }

    @Test
    fun testMeasurementDeserialization(){
        val result: MeasurementJson = objectMapper.readValue<MeasurementJson>(test_json)

        Assert.assertEquals(result.id, "5cf5f17744ea8503029dca16")
        Assert.assertEquals(result.time, LocalDateTime.of(2019, 6, 3, 7, 57, 50))
        Assert.assertEquals(result.point.x, 80.20736607838711, 0.1)
        Assert.assertEquals(result.point.y, 13.007891112984005, 0.1)
        Assert.assertEquals(13, result.phenomenons.size)
    }

    companion object {
        private const val test_json =
                """{
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [
                                80.20736607838711,
                                13.007891112984005
                            ]
                        },
                        "properties": {
                        "id": "5cf5f17744ea8503029dca16",
                        "time": "2019-06-03T07:57:50Z",
                        "phenomenons": {
                        "GPS Speed": {
                        "value": 50.44070574714897,
                        "unit": "km/h"
                        },
                        "GPS Altitude": {
                        "value": -61.1641788482666,
                        "unit": "m"
                        },
                        "GPS PDOP": {
                        "value": 1.4,
                        "unit": "precision"
                        },
                        "Intake Pressure": {
                        "value": 107.92105132341385,
                        "unit": "kPa"
                        },
                        "GPS HDOP": {
                        "value": 1.1,
                        "unit": "precision"
                        },
                        "Intake Temperature": {
                        "value": 26.988888109102845,
                        "unit": "c"
                        },
                        "GPS VDOP": {
                        "value": 0.9,
                        "unit": "precision"
                        },
                        "Engine Load": {
                        "value": 51.097970896517154,
                        "unit": "%"
                        },
                        "MAF": {
                        "value": 8.895874548784743,
                        "unit": "l/s"
                        },
                        "Rpm": {
                        "value": 350.2509519457817,
                        "unit": "u/min"
                        },
                        "GPS Accuracy": {
                        "value": 26.132002506284607,
                        "unit": "%"
                        },
                        "Speed": {
                        "value": 96.37358410656452,
                        "unit": "km/h"
                        },
                        "GPS Bearing": {
                        "value": 87.93432470862535,
                        "unit": "deg"
                        }
                        }
                    }
                }"""
    }
}