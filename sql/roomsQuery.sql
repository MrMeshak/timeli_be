WITH 
pricePoliciesData AS (
	SELECT pp.roomId,
	json_agg(
		jsonb_build_object(
			'id', pp.id,
			'dayOfWeek', pp.dayOfWeek,
			'price', pp.price,
			'mask', pp.mask,
			'roomId', pp.roomId
		)
	) AS pricePolicies
	FROM pricePolicies pp
	GROUP BY pp.roomId
),
availabilityData AS (
	SELECT av.roomId,
	json_agg(
		jsonb_build_object(
			'id', av.id,
			'dayOfWeek', av.dayOfWeek,
			'mask', av.mask,
			'roomId', av.roomId
		)
	) as availability
	FROM availability av
	GROUP BY av.roomId
)
slotData AS (
	SELECT 
		s.roomId,
		json_agg(
			json_build_object(
				'id', s.id,
				'slotDate', s.slotDate,
				'slotIndex', s.slotIndex,
				'status', s.status,
				'roomId', s.roomId,
				'bookingId', s.bookingId,
				'userId', s.userId
			)
		) as slots
		FROM slots s
		WHERE slotDate = ''
)
SELECT
  r.id,
  r.name,
  r.displayName,
  r.roomcode,
  r.capacity,
  r.slotsize,
  r.defaultPrice,
  jsonb_build_object('id', rt.id, 'name', rt.name) AS roomType,
  jsonb_build_object(
  	'id', l.id, 
  	'name', l.name, 
  	'description', l.description,
  	'street', l.street,
  	'city', l.city,
  	'state', l.state,
  	'country', l.country,
  	'postCode', l.postCode
  ) AS location,
  COALESCE(ppd.pricePolicies, '[]') AS pricePolicies,
  COALESCE(ad.availability, '[]') AS availability
FROM rooms r
JOIN roomTypes rt ON r.roomTypeId = rt.id
JOIN locations l ON r.locationid = l.id
LEFT JOIN pricePoliciesData ppd ON ppd.roomId = r.id
LEFT JOIN availabilityData ad ON ad.roomId = r.id
WHERE r.roomTypeId = 'badminton'; 

