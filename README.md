# custom-property-filter

Custom implementation of `SimpleBeanPropertyFilter` from Spring framework.

Use this if you need to customize the serialised JSON response of you server. 

###Usage

You send:
```json
{
  "id": "123",
  "name": "some_name",
  "description": "some description",
  "children": [
    {
      "id": "child_0",
      "name": "child_name",
      "description": "child_description"
    },
    {
      "id": "child_1",
      "name": "child_name",
      "description": "child_description"
    }
  ]
}
```

But your client wants to receive reduced data:

```json
{
  "id": "123",
  "name": "some_name",
  "children": [
    {
      "id": "child_0"
    },
    {
      "id": "child_1"
    }
  ]
}
```

The client provides request parameter "fields" with the following value:

```
"id","name","children:id"
```

If he wants to received all fields of "children" he can use "*":

```
"id","name","children:*"
```

FieldListNormalizer will add missing fields which are required by BeanPropertyFilter to work correctly.
