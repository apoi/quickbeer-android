package quickbeer.android.feature.profile

import quickbeer.android.data.state.StateMapper

class StateListSizeMapper : StateMapper<List<Any>, Int>({ list -> list.size })
