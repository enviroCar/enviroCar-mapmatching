package org.envirocar.processing.mapmatching.serde

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Test

class PhenomenonDeserializerTest {

    lateinit var objectMapper: ObjectMapper

    @Before
    fun init() {
        objectMapper = ObjectMapper()
                .registerModule(EnviroCarJSONModule())
    }

    @Test
    fun testPhenomenonDeserialization() {
//        val listMyData = Types.newParameterizedType(List::class.java, PhenomenonJson::class.java)
//        val type: JavaType = object : TypeToken<List<PhenomenonJson>>() {}.type
//        val result: List<PhenomenonJson> = objectMapper.readValue<List<PhenomenonJson>>(test_json)
    }

    companion object {
        private val test_json = """{
            'GPS Speed': {
                'value': 50.44070574714897,
                'unit': 'km/h'
            },
            'GPS Altitude': {
                'value': -61.1641788482666,
                'unit': 'm'
            },
            'GPS PDOP': {
                'value': 1.4,
                'unit': 'precision'
            },
            'Intake Pressure': {
                'value': 107.92105132341385,
                'unit': 'kPa'
            },
            'GPS HDOP': {
                'value': 1.1,
                'unit': 'precision'
            },
            'Intake Temperature': {
                'value': 26.988888109102845,
                'unit': 'c'
            },
            'GPS VDOP': {
                'value': 0.9,
                'unit': 'precision'
            },
            'Engine Load': {
                'value': 51.097970896517154,
                'unit': '%'
            },
            'MAF': {
                'value': 8.895874548784743,
                'unit': 'l/s'
            },
            'Rpm': {
                'value': 350.2509519457817,
                'unit': 'u/min'
            },
            'GPS Accuracy': {
                'value': 26.132002506284607,
                'unit': '%'
            },
            'Speed': {
                'value': 96.37358410656452,
                'unit': 'km/h'
            },
            'GPS Bearing': {
                'value': 87.93432470862535,
                'unit': 'deg'
            }
        }"""
    }
}