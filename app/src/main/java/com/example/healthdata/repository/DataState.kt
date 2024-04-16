package com.example.healthdata.repository

/**
 * A generic class that holds the current state of the underlying data
 *
 * Not every feature would need to work with all possible states of data and may work just fine with
 * a subset of these states, but keeping all possible states for completeness
 */
enum class DataState {
    /**
     * The data provider doesn't have the data and it's not even requested
     */
    UNAVAILABLE,

    /**
     * The data provider doesn't have the data at the moment but is trying to load
     */
    LOADING,

    /**
     * The data is ready and up to date, this will be used upon successful API response
     */
    READY,

    /**
     * The data provider doesn't have the data at the moment and the request to load also failed,
     * this will be used upon API error
     */
    FAILED,

    /**
     * The data provider does have the data but is also trying to refresh it by fetching new data
     */
    REFRESHING,

    /**
     * The data provider does have the data but is may not be up to date, this will be used when
     * -    API call to refresh data failed
     * -    No API call is made to refresh the data. Eg, data loaded from local DB but not refreshed
     */
    STALE
}
